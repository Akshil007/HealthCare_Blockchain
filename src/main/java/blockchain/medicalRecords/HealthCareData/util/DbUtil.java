package blockchain.medicalRecords.HealthCareData.util;

import blockchain.medicalRecords.HealthCareData.model.Appointment;
import blockchain.medicalRecords.HealthCareData.model.SQL_Details;
import blockchain.medicalRecords.HealthCareData.model.User_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;


@Service
public class DbUtil {
//    public static String sql_details.getUn() = "akshil";
//    public static String sql_details.getPass() = "akshil99";

    @Autowired
    SQL_Details sql_details;
    
    public int registerNewUser(User_details curr) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());

            String sql = "INSERT INTO user_details VALUES (NULL," +
                    "\"" + curr.getFirst_name() +
                    "\", \"" + curr.getLast_name() +
                    "\", \"" + curr.getUser_name() +
                    "\" , \"" + curr.getEmail_id() +
                    "\", \"" + curr.getUser_type() +
                    "\",\"" + curr.getPassword() + "\")";
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
        } catch (Exception e) {
            System.out.println("registerNewUser exception :: "+ e);
            return -1;
        }


        return 1;
    }

    public User_details getUserDetails(String user_name) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql = "SELECT * FROM user_details where user_name = \'" + user_name + "\'";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();

        User_details curr = new User_details(rs.getInt("user_id")
                ,rs.getString("first_name")
                ,rs.getString("last_name")
                ,rs.getString("user_name")
                ,rs.getString("email_id")
                ,rs.getString("user_type")
                ,"111111"
        );

        return curr;
    }

    public String getAvailableIp() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql = "Select IP from available_ips";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getString("IP");
    }

    public void enterIp(String ip) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql = "INSERT INTO available_ips VALUES (\'"+ip+"\')";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    public int bookAppoit(Appointment curr) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
            String sql = "INSERT INTO appointments (appointment_id, pid, did, status, description) VALUES (\'"+ curr.getAppointment_id() + "\'," + curr.getPid()
                    + "," + curr.getDid()
                    + ",\'" + curr.getStatus() + "\'"
                    + ",\'" + curr.getDescription() + "\')" ;
            System.out.println(sql);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
        } catch (Exception e) {
            System.out.println("bookAppoit error :: " + e);
            return -1;
        }

        return 1;

    }

    public ArrayList<Appointment> getAppointments(int id, boolean isDoctor) throws Exception {
        ArrayList<Appointment> data = new ArrayList<Appointment>();

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql;
        if(isDoctor) {
            sql = "Select * from appointments where did = "+id+" order by 4 desc";
        } else {
            sql = "Select * from appointments where pid = "+id+" order by 4 desc";
        }

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()) {
            data.add(new Appointment(rs.getString("appointment_id"),rs.getInt("pid"), rs.getInt("did"), rs.getString("dateAndTime"), rs.getString("status"), rs.getString("description")));
        }

        return data;

    }

    public void updateAppointStatus(String aid,String status) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql = "UPDATE appointments SET status = \'"+status+"\' where appointment_id = \'" + aid + "\'";
        System.out.println(sql);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    public ArrayList<User_details> getDoctorPermissions(int user_id) throws Exception {
        ArrayList<User_details> data = new ArrayList<User_details>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql = "Select * from user_details where user_id  in ( select did from permission where pid = "+user_id+") and user_type = 'doctor'";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while(rs.next()) {
            data.add(new User_details(
                    rs.getInt("user_id")
                   ,rs.getString("first_name")
                   ,rs.getString("last_name")
                   ,rs.getString("user_name")
                   ,rs.getString("email_id")
                   ,rs.getString("user_type")
                    ,"11111"
            ));
        }

        return data;
    }

    public void revokePermissionAction(int pid, int did) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql = "DELETE FROM permission where pid="+pid+ " and did=" +did;
        System.out.println(sql);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    public int giveDoctorPermission(int pid, int did) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
            String sql = "INSERT INTO permission VALUES("+pid+","+did+")";
            System.out.println(sql);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
        } catch (Exception e) {
            System.out.println("Invalid did " + e);
            return -1;
        }

        return 1;

    }

    public ArrayList<String> getPidPermissions(int did) throws Exception {
        ArrayList<String> data = new ArrayList<String>();

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sql_details.getUrl(),sql_details.getUn(),sql_details.getPass());
        String sql = "SELECT pid FROM permission where did = "+did;
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        System.out.println(sql);
        while(rs.next()) {
            data.add(rs.getString("pid"));
        }

        return data;
    }

}
