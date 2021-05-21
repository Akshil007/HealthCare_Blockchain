package blockchain.medicalRecords.HealthCareData.controller;


import blockchain.medicalRecords.HealthCareData.model.*;
import blockchain.medicalRecords.HealthCareData.services.MyUserDetailsService;
import blockchain.medicalRecords.HealthCareData.util.DbUtil;
import blockchain.medicalRecords.HealthCareData.util.JwtUtil;
import blockchain.medicalRecords.HealthCareData.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.json.*;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;


@Controller
public class RestController {

    private static final Logger logger = LoggerFactory.getLogger(MiningWork.class);

    @Autowired
    private AuthenticationManager authManager;


    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private DbUtil dbUtil;

    @Autowired
    private MiscUtil miscUtil;

    @Autowired
    private RestTemplate restTemplate;

    //... Helper functions

    void MenuDetails(HttpServletRequest req, Model theModel) throws Exception {
        Cookie[] cookies = req.getCookies();
        ArrayList<ArrayList<String>> ops;

        if(cookies != null) {
            for(Cookie c : cookies) {
                if(c.getName().equals("abcHospitalAppUser")) {
                    theModel.addAttribute("userName", c.getValue());
                    //Get the details to be displayed on userProfile
                    User_details curr = dbUtil.getUserDetails(c.getValue());
                    theModel.addAttribute("user_details", curr);
                } else if(c.getName().equals("abcHospitalAppUserType")) {
                    ops = miscUtil.getMenu(c.getValue());
                    theModel.addAttribute("ops", ops);
                }
            }
        }
    }

    void AppointmentDetails(int user_id, Model theModel, boolean isDoctor) throws Exception {
        ArrayList<Appointment> data = dbUtil.getAppointments(user_id, isDoctor);
        theModel.addAttribute("appointmentDetails", data);
    }

    void PermissionsGiven(int user_id, Model theModel) throws Exception {
        ArrayList<User_details> data = dbUtil.getDoctorPermissions(user_id);
        theModel.addAttribute("givenPermissions", data);
    }

    //...

    @GetMapping("/login")
    public String login(Model theModel) {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerNew(HttpServletRequest req, Model theModel) {

        User_details curr = new User_details(0
                , req.getParameter("first_name")
                , req.getParameter("last_name")
                , req.getParameter("user_name")
                , req.getParameter("email_id")
                , req.getParameter("user_type")
                , req.getParameter("password"));

        logger.info("user registred:"+curr.toString());

        if(dbUtil.registerNewUser(curr) == 1) {
            return "login";
        } else {
            theModel.addAttribute("isError", "Error creating new user. User Name already exists");
            return "register";
        }


    }

    @GetMapping("/publicDNSEntry")
    public String publicDNSEntry() { return "publicDNSEntry"; }

    @PostMapping("/publicDNSEntry")
    public String enterPublicDNS(HttpServletRequest req) throws Exception {
        dbUtil.enterIp(req.getParameter("ipaddress"));
        return "login";
    }



    @GetMapping("/userProfile")
    public String userProfilePage(HttpServletRequest req, Model theModel) throws Exception {

        MenuDetails(req, theModel);

        return "userProfile";
    }


    @GetMapping("/viewPatientData")
    public String viewPatientData(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);

        User_details curr = (User_details)theModel.getAttribute("user_details");

        String targetNode = dbUtil.getAvailableIp();

        String uri = "http://"+targetNode+"/get-history/id/"+ String.valueOf(curr.getUser_id());
        String result = restTemplate.getForObject(uri, String.class);

        JSONObject responseData = new JSONObject(result);
        JSONObject medicalHistory = responseData.getJSONObject("medicalHistory");
        JSONArray records = medicalHistory.getJSONArray("Records");

        ArrayList<Record_data> data = new ArrayList<Record_data>();

        for (int i=0; i < records.length(); i++) {
            data.add(new Record_data(records.getJSONObject(i).getString("doctor")
                    , records.getJSONObject(i).getString("patient")
                    , records.getJSONObject(i).getString("description")
                    , records.getJSONObject(i).getString("prescription")
                    , records.getJSONObject(i).getString("RecordId"))
            );
        }

        theModel.addAttribute("blockchain_data",data);
        theModel.addAttribute("nodeUrl","http://"+targetNode);

        return "viewPatientData";

    }

    @GetMapping("/bookDoctor")
    public String bookDoctor(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        ArrayList<User_details> availableDoctors = dbUtil.getDoctors();
        theModel.addAttribute("availableDoctors",availableDoctors);
        return "bookDoctor";
    }

    @PostMapping("/bookDoctor")
    public String bookDocAppointment(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details temp = (User_details) theModel.getAttribute("user_details");
        UUID aid = UUID.randomUUID();
        String appointment_id = aid.toString();

        Appointment curr = new Appointment(
                appointment_id
                , temp.getUser_id()
                , Integer.parseInt(req.getParameter("doctorid"))
                , null
                , "Active"
                , req.getParameter("description")
        );

        if(dbUtil.bookAppoit(curr) == -1) {
            theModel.addAttribute("isError", "Error booking doctor");
            return "bookDoctor";
        }

        AppointmentDetails(temp.getUser_id(),theModel, temp.getUser_type().equals("doctor") ? true : false);

        return "viewPatientAppointment";
    }


    @GetMapping("/viewAppointments")
    public String viewPatientAppointmentPage(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details temp = (User_details) theModel.getAttribute("user_details");
        AppointmentDetails(temp.getUser_id(),theModel, temp.getUser_type().equals("doctor") ? true : false);

        if(temp.getUser_type().equals("doctor")) {
            return "viewDoctorAppointment";
        } else {
            return "viewPatientAppointment";
        }


    }

    @GetMapping("/cancelAppointment")
    public String cancelAppointment(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details temp = (User_details) theModel.getAttribute("user_details");
        dbUtil.updateAppointStatus(req.getParameter("aid"),"Cancel");
        AppointmentDetails(temp.getUser_id(),theModel, temp.getUser_type().equals("doctor") ? true : false);
        return "viewPatientAppointment";
    }

    @GetMapping("/doctorPermissions")
    public String doctorPermissionsPage(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details temp = (User_details) theModel.getAttribute("user_details");
        PermissionsGiven(temp.getUser_id(), theModel);
        return "doctorPermissions";
    }

    @PostMapping("/givePermission")
    public String givePermissionMethod(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details temp = (User_details) theModel.getAttribute("user_details");
        if(dbUtil.giveDoctorPermission(temp.getUser_id(), Integer.parseInt(req.getParameter("did"))) == -1) {
            theModel.addAttribute("isError", "Invalid Doctor ID");
        }
        PermissionsGiven(temp.getUser_id(), theModel);
        return "doctorPermissions";
    }

    @GetMapping("/revokePermission")
    public String revokePermissionAction(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details temp = (User_details) theModel.getAttribute("user_details");
        dbUtil.revokePermissionAction(temp.getUser_id(), Integer.parseInt(req.getParameter("did")));
        PermissionsGiven(temp.getUser_id(), theModel);
        return "doctorPermissions";
    }


    //...

    //... Doctor APIS

    @GetMapping("/prescribe")
    public String prescribeMethod(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details curr = (User_details)theModel.getAttribute("user_details");

        String targetNode = dbUtil.getAvailableIp();

        String uri = "http://"+targetNode+"/get-history/id/"+ req.getParameter("pid");
        String result = restTemplate.getForObject(uri, String.class);

        JSONObject responseData = new JSONObject(result);
        JSONObject medicalHistory = responseData.getJSONObject("medicalHistory");
        JSONArray records = medicalHistory.getJSONArray("Records");

        ArrayList<Record_data> data = new ArrayList<Record_data>();

        for (int i=records.length()-1; i >= 0; i--) {
            data.add(new Record_data(records.getJSONObject(i).getString("doctor")
                    , records.getJSONObject(i).getString("patient")
                    , records.getJSONObject(i).getString("description")
                    , records.getJSONObject(i).getString("prescription")
                    , records.getJSONObject(i).getString("RecordId"))
            );
        }

        theModel.addAttribute("blockchain_data",data);
        theModel.addAttribute("appointment_id",req.getParameter("aid"));
        theModel.addAttribute("patient_id",req.getParameter("pid"));
        theModel.addAttribute("nodeUrl","http://"+targetNode);

        return "prescribe";
    }

    @PostMapping("/prescribe")
    public String newPrescribe(HttpServletRequest req, Model theModel) throws Exception {

        MenuDetails(req, theModel);
        User_details temp = (User_details) theModel.getAttribute("user_details");
        AppointmentDetails(temp.getUser_id(),theModel, temp.getUser_type().equals("doctor") ? true : false);

        // update db
        System.out.println(req.getParameter(("aid")));
        dbUtil.updateAppointStatus(req.getParameter(("aid")),"Prescribed");
        // insert in blockchain
        String targetNode = dbUtil.getAvailableIp();
        String uri = "http://"+targetNode+"/record/broadcast";

        JSONObject bodyParams = new JSONObject();
        bodyParams.put("doctor",String.valueOf(temp.getUser_id()));
        bodyParams.put("patient",req.getParameter("pid"));
        bodyParams.put("description",req.getParameter("description"));
        bodyParams.put("prescription",req.getParameter("prescription"));

        String requestJson = bodyParams.toString();
        System.out.println(requestJson);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
        String answer = restTemplate.postForObject(uri, entity, String.class);
        System.out.println(answer);
        if(temp.getUser_type().equals("doctor")) {
            return "viewDoctorAppointment";
        } else {
            return "viewPatientAppointment";
        }


    }

    @GetMapping("/viewDoctorPastRecords")
    public String viewDoctorPastRecords(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details curr = (User_details)theModel.getAttribute("user_details");

        String targetNode = dbUtil.getAvailableIp();

        String uri = "http://"+targetNode+"/get-history/id/"+ String.valueOf(curr.getUser_id());
        String result = restTemplate.getForObject(uri, String.class);

        JSONObject responseData = new JSONObject(result);
        JSONObject medicalHistory = responseData.getJSONObject("medicalHistory");
        JSONArray records = medicalHistory.getJSONArray("Records");

        ArrayList<Record_data> data = new ArrayList<Record_data>();

        for (int i=0; i < records.length(); i++) {
            data.add(new Record_data(records.getJSONObject(i).getString("doctor")
                    , records.getJSONObject(i).getString("patient")
                    , records.getJSONObject(i).getString("description")
                    , records.getJSONObject(i).getString("prescription")
                    , records.getJSONObject(i).getString("RecordId"))
            );
        }

        theModel.addAttribute("blockchain_data",data);
        theModel.addAttribute("nodeUrl","http://"+targetNode);

        return "viewDoctorRecords";

    }

    @GetMapping("viewPatientDataDoc")
    public String viewPatientDataDoc(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details curr = (User_details)theModel.getAttribute("user_details");

        ArrayList<String> pids = dbUtil.getPidPermissions(curr.getUser_id());

        theModel.addAttribute("patient_ids", pids);

        return "viewPatientDataDoc";
    }

    @GetMapping("getPatientData")
    public String getPatientDataDocSide(HttpServletRequest req, Model theModel) throws Exception {
        MenuDetails(req, theModel);
        User_details curr = (User_details)theModel.getAttribute("user_details");

        String targetNode = dbUtil.getAvailableIp();

        String uri = "http://"+targetNode+"/get-history/id/"+ req.getParameter("pid");
        String result = restTemplate.getForObject(uri, String.class);

        JSONObject responseData = new JSONObject(result);
        JSONObject medicalHistory = responseData.getJSONObject("medicalHistory");
        JSONArray records = medicalHistory.getJSONArray("Records");

        ArrayList<Record_data> data = new ArrayList<Record_data>();

        for (int i=0; i < records.length(); i++) {
            data.add(new Record_data(records.getJSONObject(i).getString("doctor")
                    , records.getJSONObject(i).getString("patient")
                    , records.getJSONObject(i).getString("description")
                    , records.getJSONObject(i).getString("prescription")
                    , records.getJSONObject(i).getString("RecordId"))
            );
        }

        theModel.addAttribute("blockchain_data",data);
        theModel.addAttribute("patient_id",req.getParameter("pid"));
        theModel.addAttribute("nodeUrl","http://"+targetNode);


        return "getPatientDataDoc";
    }

    //...


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authReq) throws Exception {
        System.out.println(authReq.getUsername() +" :: "+ authReq.getPassword());
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword())
            );

        } catch (BadCredentialsException be) {
            throw new Exception("Incorrect username / password : ", be);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authReq.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        final String userName = jwtTokenUtil.extractUsername(jwt);
        return  ResponseEntity.ok(new AuthenticationResponse(jwt));

    }

}
