package blockchain.medicalRecords.HealthCareData.util;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MiscUtil {

    public ArrayList<ArrayList<String>> getMenu(String user_type) {
        ArrayList<ArrayList<String>> haha = new ArrayList<ArrayList<String>>();
        if(user_type.equals("doctor")) {
            haha.add(new ArrayList<String>(Arrays.asList("Profile", "/userProfile")));
            haha.add(new ArrayList<String>(Arrays.asList("Appointments", "/viewAppointments ")));
            haha.add(new ArrayList<String>(Arrays.asList("Past Records", "/viewDoctorPastRecords")));
            haha.add(new ArrayList<String>(Arrays.asList("Patient Data", "/viewPatientDataDoc")));
        } else {
            haha.add(new ArrayList<String>(Arrays.asList("View Profile", "/userProfile")));
            haha.add(new ArrayList<String>(Arrays.asList("View Data", "/viewPatientData")));
            haha.add(new ArrayList<String>(Arrays.asList("Book Doctor", "/bookDoctor")));
            haha.add(new ArrayList<String>(Arrays.asList("Give / Revoke Doctor Permission", "/doctorPermissions")));
            haha.add(new ArrayList<String>(Arrays.asList("View Appointments", "/viewAppointments")));
        }

        return haha;

    }

}
