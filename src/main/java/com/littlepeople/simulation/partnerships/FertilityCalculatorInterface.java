package com.littlepeople.simulation.partnerships;

import com.littlepeople.core.model.Person;

public interface FertilityCalculatorInterface {
    boolean isFertile(Person person);
    double calculateMonthlyConceptionProbability(Person malePartner, Person femalePartner);
}
