package com.andrezamoreira.pontos.models;

import java.util.ArrayList;

public class User {

        private String id;
        private String email;
        private String name;
        private ArrayList<Membership> memberships;
        private String status;


        public String getID() {
                return id;
        }

        public void setID(String value) {
                this.id = value;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String value) {
                this.email = value;
        }

        public String getName() {
                return name;
        }

        public void setName(String value) {
                this.name = value;
        }

        public ArrayList<Membership> getMemberships() {
                return memberships;
        }

        public void setMemberships(ArrayList<Membership> memberships) {
                this.memberships = memberships;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String value) {
                this.status = value;
        }
}

