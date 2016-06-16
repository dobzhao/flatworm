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

package domain;

/**
 * Created by IntelliJ IDEA. User: turner Date: Jun 16, 2004 Time: 1:44:16 AM To change this template use File | Settings | File Templates.
 */
public class Dvd {
    private String sku;

    private String dualLayer;

    private double price;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDualLayer() {
        return dualLayer;
    }

    public void setDualLayer(String dualLayer) {
        this.dualLayer = dualLayer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String toString() {
        return super.toString() + "[" + sku + ", " + price + ", " + dualLayer + "]";
    }

}
