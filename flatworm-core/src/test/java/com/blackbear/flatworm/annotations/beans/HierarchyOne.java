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

package com.blackbear.flatworm.annotations.beans;

import com.blackbear.flatworm.annotations.Converter;
import com.blackbear.flatworm.annotations.DataIdentity;
import com.blackbear.flatworm.annotations.FieldIdentity;
import com.blackbear.flatworm.annotations.ForProperty;
import com.blackbear.flatworm.annotations.Line;
import com.blackbear.flatworm.annotations.Record;
import com.blackbear.flatworm.annotations.Scriptlet;
import com.blackbear.flatworm.converters.CoreConverters;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Specific hierarchy test with respect to records and lines and the manner in which beans are built
 *
 * @author Alan Henson
 */
@Data
@Record(name = "HierarchyOne", converters = {@Converter(name = "test", clazz = CoreConverters.class, methodName = "convertChar", returnType = String.class), @Converter(name = "test2", clazz = CoreConverters.class, methodName = "convertLong", returnType = Long.class)})
public class HierarchyOne {
    
    @Line(forProperty = @ForProperty(
            enabled = true, 
            identity = @DataIdentity(fieldIdentity = 
            @FieldIdentity(
                    enabled = true,
                    matchIdentities = {"START", "END"})))) 
    private List<HierarchyTwoParentOne> childList = new ArrayList<>();
}
