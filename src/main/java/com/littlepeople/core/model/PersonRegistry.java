package com.littlepeople.core.model;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * CONFIDENTIAL Privileged Business Information - Do Not Release.
 *   Copyright by Körber Supply Chain  Logistics GmbH   2024.
 *   <p>
 *   All rights reserved. This computer program is protected by copyright laws and
 *   international treaties. This file contains Trade Secrets of Körber Supply Chain  Logistics GmbH.
 *   With the receipt of this file, the receiver  agrees to keep strictly confidential, all
 *   Trade Secrets of Körber Supply Chain  Logistics GmbH contained herein. Copying this file, giving
 *   it to others, and the use or communication of the contents thereof, is
 *   forbidden without express authority. Offenders are liable to the payment of
 *   damages. All rights are reserved especially in the event of the grant of a
 *   patent or the registration of a utility model or design.
 *
 */
public class PersonRegistry {
    private static final Map<String, Person> personRegistry = new ConcurrentHashMap<>();
    static public Map<String, Person> getPersonRegistry() {
        return personRegistry;
    }
    public static Person get(String id) {
        return personRegistry.get(id);
    }
    public static Person get(UUID id) {
        return get(id.toString());
    }
    public static void put(Person person) {
        personRegistry.put(person.getId().toString(), person);
    }

}
