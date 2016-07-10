package com.tcs.cbademo.weathergen.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * From January to December as Enum
 * @author tinoj
 *
 */
public enum Months {
    JANUARY(0) ,
    FEBRUARY(1),
    MARCH(2),
    APRIL(3),
    MAY(4),
    JUNE(5),
    JULY(6),
    AUGUST(7),
    SEPTEMBER(8),
    OCTOBER(9),
    NOVEMBER(10),
    DECEMBER(11);
	
	private int monthCode;
	
	Months(int monthCode) {
		this.monthCode = monthCode;
	}
	
	private static final Map<Integer, Months> monthsByValue = new HashMap<Integer, Months>();

    static {
        for (Months type : Months.values()) {
        	monthsByValue.put(type.monthCode, type);
        }
    }

    /**
     * Converts the month code in 0-11 format to enum
     * @param value month in 0-11 format
     * @return enum Months
     */
    public static Months getMonthforValue(int value) {
        return monthsByValue.get(value);
    }
}
