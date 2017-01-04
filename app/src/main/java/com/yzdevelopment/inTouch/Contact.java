package com.yzdevelopment.inTouch;

import android.graphics.Bitmap;

public class Contact {
    private String display_name = null;
    private String home_phone = null;
    private String work_phone = null;
    private String mobile_phone = null;
    private String email = null;
    private String company = null;
    private String jobTitle = null;
    private String imageUri = null;

    public Contact() {
    }

    public Contact(String display_name, String mobile_phone, String home_phone,
                   String work_phone, String email, String company, String jobTitle, String imageUri) {
        this.display_name = display_name;

        this.home_phone = home_phone.equals("__empty") ?  null : home_phone;
        this.work_phone = work_phone.equals("__empty") ?  null : work_phone;
        this.mobile_phone = mobile_phone.equals("__empty") ?  null : mobile_phone;
        this.email = email.equals("__empty") ?  null : email;
        this.company = company.equals("__empty") ?  null : company;
        this.jobTitle = jobTitle.equals("__empty") ?  null : jobTitle;
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setHome_phone(String home_phone) {
        this.home_phone = home_phone;
    }

    public void setWork_phone(String work_phone) {
        this.work_phone = work_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getHome_phone() {
        return home_phone;
    }

    public String getWork_phone() {
        return work_phone;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public String getEmail() {
        return email;
    }

    public String dispInAlert() {
        if (mobile_phone != null) {
            return display_name + "\nMobile: " + mobile_phone + "\n";
        }
        if (home_phone != null) {
            return display_name + "\nHome: " + home_phone + "\n";
        }
        if (work_phone != null) {
            return display_name + "\nWork: " + work_phone + "\n";
        }

        if (email != null) {
            return display_name + "\nEmail: " + email + "\n";
        }

//        Log.i("Yan","company is " + company);
//        Log.i("Yan","title is " + jobTitle);

        if (company != null && jobTitle != null) {
            return display_name + "\n" + company + ", " + jobTitle;
        } else if (company != null) {
            return display_name + "\n" + company;
        } else if (jobTitle != null) {
            return display_name + "\n" + jobTitle;
        }

        return null;
    }

    public String dispInConfirmationName() {
        StringBuilder sb = new StringBuilder();
        sb.append(display_name);

        if (company != null && jobTitle != null) {
            return sb.toString() + "\n" + jobTitle + ", " + company;
        } else if (company != null) {
            return sb.toString() + "\n" + company;
        } else if (jobTitle != null) {
            return sb.toString() + "\n" + jobTitle;
        }

        return sb.toString();
    }

    public String dispInConfirmationInfo() {
        StringBuilder sb = new StringBuilder();
        if (mobile_phone != null) {
            sb.append("Mobile:\t\t" + mobile_phone + "\n");
        }
        if (home_phone != null) {
            sb.append("Home:\t\t" + home_phone + "\n");
        }
        if (work_phone != null) {
            sb.append("Work:\t\t\t" + work_phone + "\n");
        }
        if (email != null) {
            sb.append("Email:\t\t\t" + email + "\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Contact{" +
                "display_name='" + display_name + '\'' +
                ", home_phone='" + home_phone + '\'' +
                ", work_phone='" + work_phone + '\'' +
                ", mobile_phone='" + mobile_phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }
}
