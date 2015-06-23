package com.greladesign.examples.games.mymathgame.game;

import java.util.EnumSet;

public enum Operation {
    ADD,SUBSTRACT,MULTIPLY,DIVIDE;

    public static final EnumSet<Operation> ALL = EnumSet.allOf(Operation.class);

    public static int[] toArray(EnumSet<Operation> operations) {
        if(operations == null) return new int[0];
        final int[] result = new int[operations.size()];
        int i = 0;
        for (Operation entry : operations) {
            result[i++] = (entry.ordinal());
        }
        return result;
    }
    public static EnumSet<Operation> fromArray(int[] operationIds) {
        final EnumSet<Operation> operations = EnumSet.noneOf(Operation.class);
        if(operationIds == null) return operations;

        for(int i=0; i<operationIds.length; i++){
            if(ADD.ordinal() == operationIds[i]) {
                operations.add(ADD);
            }
            if(SUBSTRACT.ordinal() == operationIds[i]) {
                operations.add(SUBSTRACT);
            }
            if(MULTIPLY.ordinal() == operationIds[i]) {
                operations.add(MULTIPLY);
            }
            if(DIVIDE.ordinal() == operationIds[i]) {
                operations.add(DIVIDE);
            }
        }
        return operations;
    }
}