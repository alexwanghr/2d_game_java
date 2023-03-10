package com.journaldev.design.observer;

/*
// Tutorial //
Observer Design Pattern in Java
https://www.digitalocean.com/community/tutorials/observer-design-pattern-in-java
*/

import java.io.IOException;

public interface Observer {

    //method to update the observer, used by subject
    public void update();

    //attach with subject to observe
    public void setSubject(Subject sub);
}