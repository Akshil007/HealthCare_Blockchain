package blockchain.medicalRecords.HealthCareData.model;

public class Record_data {
    private String doctor;
    private String patient;
    private String description;
    private String prescription;
    private String RecordId;

    public Record_data(String doctor, String patient, String description, String prescription, String recordId) {
        this.doctor = doctor;
        this.patient = patient;
        this.description = description;
        this.prescription = prescription;
        RecordId = recordId;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getRecordId() {
        return RecordId;
    }

    public void setRecordId(String recordId) {
        RecordId = recordId;
    }
}
