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

    @Autowired
    SQL_Details sqlDetails;

    public int registerNewUser(User_details curr) {
        Connection con;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());

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
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
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
        con.close();
        return curr;
    }

    public String getAvailableIp() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
        String sql = "Select IP from available_ips";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String returnIP = rs.getString("IP");
        con.close();
        return returnIP;
    }

    public void enterIp(String ip) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
        String sql = "INSERT INTO available_ips VALUES (\'"+ip+"\')";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    public int bookAppoit(Appointment curr) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
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
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
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

        con.close();

        return data;

    }

    public void updateAppointStatus(String aid,String status) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
        String sql = "UPDATE appointments SET status = \'"+status+"\' where appointment_id = \'" + aid + "\'";
        System.out.println(sql);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    public ArrayList<User_details> getDoctorPermissions(int user_id) throws Exception {
        ArrayList<User_details> data = new ArrayList<User_details>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
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

        con.close();

        return data;
    }

    public void revokePermissionAction(int pid, int did) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
        String sql = "DELETE FROM permission where pid="+pid+ " and did=" +did;
        System.out.println(sql);
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.close();
    }

    public int giveDoctorPermission(int pid, int did) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
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
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
        String sql = "SELECT pid FROM permission where did = "+did;
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        System.out.println(sql);
        while(rs.next()) {
            data.add(rs.getString("pid"));
        }

        con.close();

        return data;
    }

    public ArrayList<User_details> getDoctors() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
        String sql = "SELECT * FROM user_details where user_type = \'doctor\'";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ArrayList<User_details> data = new ArrayList<User_details>();
        while(rs.next()){
            data.add(new User_details(rs.getInt("user_id")
                    ,rs.getString("first_name")
                    ,rs.getString("last_name")
                    ,rs.getString("user_name")
                    ,rs.getString("email_id")
                    ,rs.getString("user_type")
                    ,"11111"));
        }

        con.close();

        return data;
    }

    public void createTables() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con= DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());

        String sql1 = "create table user_details (\n" +
                "    user_id int NOT NULL AUTO_INCREMENT,\n" +
                "    first_name VARCHAR(255),\n" +
                "    last_name VARCHAR(255),\n" +
                "    user_name VARCHAR(255) UNIQUE,\n" +
                "    email_id VARCHAR(255) UNIQUE,\n" +
                "    user_type VARCHAR(255),\n" +
                "    password VARCHAR(255),\n" +
                "    PRIMARY KEY (user_id,user_name)\n" +
                ")";
        String sql2 = "CREATE TABLE available_ips (\n" +
                "    IP VARCHAR(255),\n" +
                "    PRIMARY KEY(IP)\n" +
                ")";
        String sql3 = "CREATE TABLE appointments (\n" +
                "    appointment_id VARCHAR(255),\n" +
                "    pid INT,\n" +
                "    did INT,\n" +
                "    dateAndTime timestamp not null default current_timestamp,\n" +
                "    status VARCHAR(255),\n" +
                "    description VARCHAR(255),\n" +
                "    PRIMARY KEY(appointment_id)\n" +
                ");";
        String sql4 = "CREATE TABLE permission (\n" +
                "    pid int,\n" +
                "    did int,\n" +
                "    PRIMARY KEY (pid,did),\n" +
                "    FOREIGN KEY (pid) REFERENCES user_details(user_id),\n" +
                "    FOREIGN KEY (did) REFERENCES user_details(user_id)\n" +
                ")";

        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql1);
        stmt.executeUpdate(sql2);
        stmt.executeUpdate(sql3);
        stmt.executeUpdate(sql4);
        con.close();
    }

}
