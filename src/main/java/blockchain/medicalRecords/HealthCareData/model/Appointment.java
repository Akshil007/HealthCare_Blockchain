package blockchain.medicalRecords.HealthCareData.model;

public class Appointment {
    private String appointment_id;
    private int pid;
    private int did;
    private String dateAndTime;
    private String status;
    private String description;

    public Appointment(String appointment_id, int pid, int did, String dateAndTime, String status, String description) {
        this.appointment_id = appointment_id;
        this.pid = pid;
        this.did = did;
        this.dateAndTime = dateAndTime;
        this.status = status;
        this.description = description;
    }

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
