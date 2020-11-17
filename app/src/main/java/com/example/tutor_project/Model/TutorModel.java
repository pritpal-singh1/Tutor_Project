package com.example.tutor_project.Model;

public class TutorModel {

    String title, address, category, tutor_class, subject, days, city ,number_of_students, salary, contact, createdAt, createdBy,type;

    public TutorModel() {
    }

    public TutorModel(String title, String address,
                      String category, String tutor_class, String subject,
                      String days, String city, String number_of_students, String salary, String contact,
                      String createdAt, String createdBy, String type) {
        this.title = title;
        this.address = address;
        this.category = category;
        this.tutor_class = tutor_class;
        this.subject = subject;
        this.days = days;
        this.city = city;
        this.number_of_students = number_of_students;
        this.salary = salary;
        this.contact = contact;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTutor_class() {
        return tutor_class;
    }

    public void setTutor_class(String tutor_class) {
        this.tutor_class = tutor_class;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber_of_students() {
        return number_of_students;
    }

    public void setNumber_of_students(String number_of_students) {
        this.number_of_students = number_of_students;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


