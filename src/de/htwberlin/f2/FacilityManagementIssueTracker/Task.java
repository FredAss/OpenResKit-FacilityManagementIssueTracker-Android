package de.htwberlin.f2.FacilityManagementIssueTracker;

import com.j256.ormlite.field.DatabaseField;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task implements Serializable {

    @DatabaseField(id = true)
    @JsonProperty("Id")
    private int Id = 0;

    public Task(String description, String location, String image,
                Date reportDate, Date dueDate, Boolean isTaskFixed) {
        super();
        Description = description;
        Location = location;
        Image = image;
        ReportDate = reportDate;
        DueDate = dueDate;
        IsTaskFixed = isTaskFixed;
    }

    public Task() {
        super();
    }

    @DatabaseField
    @JsonProperty("Description")
    private String Description = "";

    @DatabaseField
    @JsonProperty("Location")
    private String Location = "";

    @DatabaseField
    @JsonProperty("Image")
    private String Image;

    @DatabaseField
    @JsonProperty("ReportDate")
    private Date ReportDate = new Date();

    @DatabaseField
    @JsonProperty("DueDate")
    private Date DueDate = new Date();

    @DatabaseField
    @JsonProperty("IsTaskFixed")
    private Boolean IsTaskFixed = false;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Date getReportDate() {
        return ReportDate;
    }

    public void setReportDate(Date reportDate) {
        ReportDate = reportDate;
    }

    public Date getDueDate() {
        return DueDate;
    }

    public void setDueDate(Date dueDate) {
        DueDate = dueDate;
    }

    public Boolean getIsTaskFixed() {
        return IsTaskFixed;
    }

    public void setIsTaskFixed(Boolean isTaskFixed) {
        IsTaskFixed = isTaskFixed;
    }

    public String toString() {
        return "Task [Description=" + getDescription() + "]";
//		return "\nMangel: " + getDescription() + "\nOrt: " + getLocation() + "\nFällig am: " + getDueDate();
    }
}
