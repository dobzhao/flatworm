/*
 * Flatworm - A Java Flat File Importer/Exporter Copyright (C) 2004 James M. Turner.
 * Extended by James Lawrence 2005
 * Extended by Josh Brackett in 2011 and 2012
 * Extended by Alan Henson in 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.blackbear.flatworm.converters;

import com.blackbear.flatworm.Util;
import com.blackbear.flatworm.config.ConversionOptionBO;
import com.blackbear.flatworm.errors.FlatwormParserException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * {@code CoreConverters} contains methods to convert the most commonly encountered text types to native Java types. It can be used as the
 * {@code class} parameter in a Flatworm {@code converter} tag, with one of the public methods listed below as the {@code method} parameter.
 * All converters (included the ones listed here and any supplied by the user) should expect to accept and handle the following {@code
 * conversion-option}s:  <dl> <dt>{@code justify}</dt> <dd>Defines the justification rule used to strip/add pad characters to this field.
 * Used in conjunction with the {@code pad-character} option. Valid values are: <ul> <li>{@code left} to strip the right hand side of the
 * field of pad chars</li> <li>{@code right} to strip the left hand side of the field of pad chars</li> <li>{@code both} to strip both end
 * of the field of pad chars</li> </ul> The default if no value is specified is {@code both}</dd> <dt>{@code pad-character}</dt> <dd>Defines
 * the character to be used in conjunction with the {@code justify} option when removing or adding pad characters from a field. The default
 * value is the space character.</dd> <dt>{@code default-value}</dt> <dd>Defines the default value to be used if the field is empty after
 * being stripped of pad characters.</dd> </dl>
 *
 * NOTE: This class must remain threadsafe.
 *
 * @author James M. Turner
 * @version $Id: CoreConverters.java,v 1.8 2009/12/07 00:50:53 dderry Exp $
 */
@Slf4j
public class CoreConverters {

    /**
     * Conversion function for {@code String}, returns the source string with padding removed if requested.
     *
     * @param str     The source string
     * @param options The conversion-option values for the field
     * @return The string with padding removed
     */

    public String convertChar(String str, Map<String, ConversionOptionBO> options) {
        // nothing extra do do, since convHelper calls removePadding now
        return str;
    }

    /**
     * Object to String conversion function.
     *
     * @param obj     source object
     * @param options The conversion-option values for the field
     * @return The string result
     */
    public String convertChar(Object obj, Map<String, ConversionOptionBO> options) {
        return obj.toString();
    }

    /**
     * Conversion function for {@code Date}, returns the source string with padding removed if requested, converted into a date.  In
     * addition to the standard conversion options, dates also support the following:  <dl> <dt>{@code format}</dt> <dd>A date format string
     * in {@code SimpleDateFormat} syntax that defines the format to expect, default is yyyy-MM-dd.</dd> </dl>
     *
     * @param str     The source string
     * @param options The conversion-option values for the field
     * @return The converted date
     * @throws FlatwormParserException if the date fails to parse correctly.
     */
    public Date convertDate(String str, Map<String, ConversionOptionBO> options)
            throws FlatwormParserException {
        try {
            String format = Util.getValue(options, "format");

            SimpleDateFormat sdf;
            if (str.length() == 0)
                return null;
            if (format == null)
                format = "yyyy-MM-dd";
            sdf = new SimpleDateFormat(format);
            return sdf.parse(str);
        } catch (ParseException ex) {
            log.error("Failed to parse date", ex);
            throw new FlatwormParserException(str);
        }
    }

    /**
     * Date to String conversion function.
     *
     * @param obj     source object of type {@link Date}.
     * @param options The conversion-option values for the field
     * @return the string result
     */
    public String convertDate(Object obj, Map<String, ConversionOptionBO> options) {
        Date date = (Date) obj;
        String format = Util.getValue(options, "format");
        if (obj == null)
            return null;
        if (format == null)
            format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Conversion function for {@code Double}, returns the source string with padding removed if requested, converted into a double.
     *
     *
     * In addition to the standard conversion options, doubles also support the following:  <dl> <dt>{@code decimal-implied}</dt> <dd>If set
     * to {@code true}, the decimal point is positionally implied rather than explicitly included. If set, {@code decimal-places} is
     * required.</dd> <dt>{@code decimal-places}</dt> <dd>The number of digits in the string which are to the right of the decimal point, if
     * {@code decimal-implied} is set.</dd> </dl>
     *
     * @param str     The source string
     * @param options The conversion-option values for the field
     * @return The converted double value
     * @throws FlatwormParserException If the source number fails to parse as a double or the decimal places option fails to parse as an
     *                                 integer value.
     */
    public Double convertDouble(String str, Map<String, ConversionOptionBO> options) throws FlatwormParserException {
        try {
            int decimalPlaces = 0;
            ConversionOptionBO conv = options.get("decimal-places");

            String decimalPlacesOption = null;
            if (null != conv)
                decimalPlacesOption = conv.getValue();

            boolean decimalImplied = "true".equals(Util.getValue(options, "decimal-implied"));

            if (decimalPlacesOption != null)
                decimalPlaces = Integer.parseInt(decimalPlacesOption);

            if (str.length() == 0)
                return 0.0D;

            if (decimalImplied)
                return Double.parseDouble(str) / Math.pow(10D, decimalPlaces);
            else
                return Double.valueOf(str);

        } catch (NumberFormatException ex) {
            log.error("Failed to parse double value", ex);
            throw new FlatwormParserException(str);
        }
    }

    /**
     * Convert a {@link Double} to a {@link String} taking into account the options provided.
     *
     * In addition to the standard conversion options, doubles also support the following:  <dl> <dt>{@code decimal-implied}</dt> <dd>If set
     * to {@code true}, the decimal point is positionally implied rather than explicitly included. If set, {@code decimal-places} is
     * required.</dd> <dt>{@code decimal-places}</dt> <dd>The number of digits in the string which are to the right of the decimal point, if
     * {@code decimal-implied} is set.</dd> </dl>
     *
     * @param obj     The {@code obj} to convert.
     * @param options The {@code ConversionOptionBO} provided.
     * @return the {@code obj} converted to a {@link String}.
     */
    public String convertDouble(Object obj, Map<String, ConversionOptionBO> options) {
        Double d = (Double) obj;
        if (d == null) {
            return null;
        }

        int decimalPlaces = 0;
        ConversionOptionBO conv = options.get("decimal-places");

        String decimalPlacesOption = null;
        if (null != conv)
            decimalPlacesOption = conv.getValue();

        boolean decimalImplied = "true".equals(Util.getValue(options, "decimal-implied"));

        if (decimalPlacesOption != null)
            decimalPlaces = Integer.parseInt(decimalPlacesOption);

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(!decimalImplied);
        format.setGroupingUsed(false);
        if (decimalImplied) {
            format.setMaximumFractionDigits(0);
            d = d * Math.pow(10D, decimalPlaces);
        } else {
            format.setMinimumFractionDigits(decimalPlaces);
            format.setMaximumFractionDigits(decimalPlaces);
        }
        return format.format(d);
    }

    /**
     * Conversion function for {@code Float}, returns the source string with padding removed if requested, converted into a float.
     *
     *
     * In addition to the standard conversion options, floats also support the following:  <dl> <dt>{@code decimal-implied}</dt> <dd>If set
     * to {@code true}, the decimal point is positionally implied rather than explicitly included. If set, {@code decimal-places} is
     * required.</dd> <dt>{@code decimal-places}</dt> <dd>The number of digits in the string which are to the right of the decimal point, if
     * {@code decimal-implied} is set.</dd> </dl>
     *
     * @param str     The source string
     * @param options The conversion-option values for the field
     * @return The converted float value
     * @throws FlatwormParserException If the source number fails to parse as a float or the decimal places option fails to parse as an
     *                                 integer value.
     */
    public Float convertFloat(String str, Map<String, ConversionOptionBO> options) throws FlatwormParserException {
        try {
            int decimalPlaces = 0;
            ConversionOptionBO conv = options.get("decimal-places");

            String decimalPlacesOption = null;
            if (null != conv)
                decimalPlacesOption = conv.getValue();

            boolean decimalImplied = "true".equals(Util.getValue(options, "decimal-implied"));

            if (decimalPlacesOption != null)
                decimalPlaces = Integer.parseInt(decimalPlacesOption);

            if (str.length() == 0)
                return 0.0F;

            if (decimalImplied)
                return Float.parseFloat(str) / (float) Math.pow(10F, decimalPlaces);
            else
                return Float.parseFloat(str);

        } catch (NumberFormatException ex) {
            log.error("Failed to parse float value", ex);
            throw new FlatwormParserException(str);
        }
    }

    /**
     * Convert a {@link Float} to a {@link String} taking into account the options provided.
     *
     * In addition to the standard conversion options, floats also support the following:  <dl> <dt>{@code decimal-implied}</dt> <dd>If set
     * to {@code true}, the decimal point is positionally implied rather than explicitly included. If set, {@code decimal-places} is
     * required.</dd> <dt>{@code decimal-places}</dt> <dd>The number of digits in the string which are to the right of the decimal point, if
     * {@code decimal-implied} is set.</dd> </dl>
     *
     * @param obj     The {@code obj} to convert.
     * @param options The {@code ConversionOptionBO} provided.
     * @return the {@code obj} converted to a {@link String}.
     */
    public String convertFloat(Object obj, Map<String, ConversionOptionBO> options) {
        Float f = (Float) obj;
        if (f == null) {
            return null;
        }

        int decimalPlaces = 0;
        ConversionOptionBO conv = options.get("decimal-places");

        String decimalPlacesOption = null;
        if (null != conv)
            decimalPlacesOption = conv.getValue();

        boolean decimalImplied = "true".equals(Util.getValue(options, "decimal-implied"));

        if (decimalPlacesOption != null)
            decimalPlaces = Integer.parseInt(decimalPlacesOption);

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(!decimalImplied);
        format.setGroupingUsed(false);
        if (decimalImplied) {
            format.setMaximumFractionDigits(0);
            f = f * (float) Math.pow(10D, decimalPlaces);
        } else {
            format.setMinimumFractionDigits(decimalPlaces);
            format.setMaximumFractionDigits(decimalPlaces);
        }
        return format.format(f);
    }

    /**
     * Conversion function for {@code Integer}, returns the source string with padding removed if requested, converted into a integer.
     *
     * @param str     The source string
     * @param options The conversion-option values for the field
     * @return The converted integer value
     * @throws FlatwormParserException If the source number fails to parse as an integer value.
     */
    public Integer convertInteger(String str, Map<String, ConversionOptionBO> options)
            throws FlatwormParserException {
        try {
            if (str.length() == 0) {
                return 0;
            } else {
                return Integer.valueOf(str);
            }
        } catch (NumberFormatException ex) {
            log.error("Failed to parse Integer", ex);
            throw new FlatwormParserException(str);
        }
    }

    /**
     * Conver an {@link Integer} to a {@link String}.
     *
     * @param obj     The {@code obj} to convert.
     * @param options The {@code ConversionOptionBO} provided.
     * @return the {@code obj} converted to a {@link String}.
     */
    public String convertInteger(Object obj, Map<String, ConversionOptionBO> options) {
        if (obj == null) {
            return null;
        }
        Integer i = (Integer) obj;
        return Integer.toString(i);
    }

    /**
     * Conversion function for {@code Long}, returns the source string with padding removed if requested, converted into a long.
     *
     * @param str     The source string
     * @param options The conversion-option values for the field
     * @return The converted long value
     * @throws FlatwormParserException If the source number fails to parse as an long value.
     */
    public Long convertLong(String str, Map<String, ConversionOptionBO> options)
            throws FlatwormParserException {
        try {
            if (str.length() == 0) {
                return 0L;
            } else {
                return Long.valueOf(str);
            }
        } catch (NumberFormatException ex) {
            log.error("Failed to parse Long", ex);
            throw new FlatwormParserException(str);
        }
    }

    /**
     * Conver an {@link Long} to a {@link String}.
     *
     * @param obj     The {@code obj} to convert.
     * @param options The {@code ConversionOptionBO} provided.
     * @return the {@code obj} converted to a {@link String}.
     */
    public String convertLong(Object obj, Map<String, ConversionOptionBO> options) {
        if (obj == null) {
            return null;
        }
        Long l = (Long) obj;
        return Long.toString(l);
    }

    /**
     * Conversion function for {@code BigDecimal}, returns the source string with padding removed if requested, converted into a big
     * decimal.
     *
     * In addition to the standard conversion options, big decimals also support the following:  <dl> <dt>{@code decimal-implied}</dt>
     * <dd>If set to {@code true}, the decimal point is positionally implied rather than explicitly included. If set, {@code decimal-places}
     * is required.</dd> <dt>{@code decimal-places}</dt> <dd>The number of digits in the string which are to the right of the decimal point,
     * if {@code decimal-implied} is set.</dd> </dl>
     *
     * @param str     The source string.
     * @param options The conversion-option values for the field.
     * @return The converted big decimal value.
     * @throws FlatwormParserException If the source number fails to parse as a big decimal or the decimal places option fails to parse as
     *                                 an integer value.
     */
    public BigDecimal convertBigDecimal(String str, Map<String, ConversionOptionBO> options)
            throws FlatwormParserException {
        try {
            int decimalPlaces = 0;
            String decimalPlacesOption = (String) Util.getValue(options, "decimal-places");
            boolean decimalImplied = "true".equals(Util.getValue(options, "decimal-implied"));

            if (decimalPlacesOption != null)
                decimalPlaces = Integer.parseInt(decimalPlacesOption);

            if (str.length() == 0)
                return new BigDecimal(0.0D);

            BigDecimal b = new BigDecimal(str);
            if (decimalImplied)
            	return b.divide(new BigDecimal(Math.pow(10,decimalPlaces)));
            else
                return b;
        } catch (NumberFormatException ex) {
            log.error("Failed to convert BigDecimal", ex);
            throw new FlatwormParserException(str);
        }
    }

    /**
     * Conver an {@link BigDecimal} to a {@link String}.
     *
     * In addition to the standard conversion options, big decimals also support the following:  <dl> <dt>{@code decimal-implied}</dt>
     * <dd>If set to {@code true}, the decimal point is positionally implied rather than explicitly included. If set, {@code decimal-places}
     * is required.</dd> <dt>{@code decimal-places}</dt> <dd>The number of digits in the string which are to the right of the decimal point,
     * if {@code decimal-implied} is set.</dd> </dl>
     *
     * @param obj     The {@code obj} to convert.
     * @param options The {@code ConversionOptionBO} provided.
     * @return the {@code obj} converted to a {@link String}.
     */
    public String convertBigDecimal(Object obj, Map<String, ConversionOptionBO> options) {
        if (obj == null) {
            return null;
        }
        BigDecimal bd = (BigDecimal) obj;
        int decimalPlaces = 0;
        String decimalPlacesOption = Util.getValue(options, "decimal-places");
        boolean decimalImplied = "true".equals(Util.getValue(options, "decimal-implied"));

        if (decimalPlacesOption != null)
            decimalPlaces = Integer.parseInt(decimalPlacesOption);

        
        BigDecimal tmp = new BigDecimal(bd.toString());
        if (decimalImplied) {
        	tmp =  tmp.multiply(new BigDecimal(Math.pow(10,decimalPlaces)));
        	tmp.setScale(0, BigDecimal.ROUND_HALF_DOWN);
        	DecimalFormat df = new DecimalFormat("0");
        	return df.format(tmp);
        } else {
        	//判断要保留几个小数点，先写2了，应该从option取
        	tmp.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        	DecimalFormat df = new DecimalFormat("0.##");
        	return df.format(tmp);
        }
    }
}