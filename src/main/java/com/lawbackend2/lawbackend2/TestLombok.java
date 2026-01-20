package com.lawbackend2.lawbackend2;

import lombok.Data;

@Data
public class TestLombok {
    private String name;
    private int age;
    
    public static void main(String[] args) {
        TestLombok test = new TestLombok();
        test.setName("Test");
        test.setAge(18);
        System.out.println("Name: " + test.getName());
        System.out.println("Age: " + test.getAge());
    }
}
