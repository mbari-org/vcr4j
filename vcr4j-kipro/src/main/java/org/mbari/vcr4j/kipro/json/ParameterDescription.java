/*
 * @(#)ParameterDescription.java   by Brian Schlining
 *
 * Copyright (c) 2016 Monterey Bay Aquarium Research Institute
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vcr4j.kipro.json;

/**
 * {
 * "param_type": "string",
 * "descriptor_type": "string",
 * "param_id": "eParamID_DisplayTimecode",
 * "param_name": "Timecode",
 * "persistence_type": "ephemeral",
 * "register_type": "excluded",
 * "factory_reset_type": "warm",
 * "relations": {},
 * "class_names": [],
 * "string_attributes": [{
 * "name": "description",
 * "value": "Used to generate synthetic param change events (eParamID_DisplayTimecode) for the webapp."
 * }, {
 * "name": "menu_number",
 * "value": "125.4"
 * }],
 * "integer_attributes": [],
 * "min_length": 0,
 * "max_length": 20,
 * "default_value": "00:00:00:00"
 * }
 * @author Brian Schlining
 * @since 2016-02-11T08:55:00
 */
public class ParameterDescription {

    private final String paramType;
    private final String descriptorType;
    private final String paramId;
    private final String paramName;
    private final String persistenceType;
    private final String registerType;
    private final String factoryResetType;
    //private final T relations;
    private final String[] classNames;
    private final Attribute<String>[] stringAttributes;
    private final Attribute<Integer>[] integerAttributes;
    private final EnumValue[] enumValues;
    private final int maxValue;
    private final int minValue;
    private final String defaultValue;
    private final int adjustBy;
    private final int scaleBy;
    private final int displyPrecision;
    private final String units;

    public ParameterDescription(int adjustBy, String paramType, String descriptorType,
            String paramId, String paramName, String persistenceType, String registerType,
            String factoryResetType, String[] classNames, Attribute<String>[] stringAttributes,
            Attribute<Integer>[] integerAttributes, EnumValue[] enumValues, int maxValue,
            int minValue, String defaultValue, int scaleBy, int displyPrecision, String units) {
        this.adjustBy = adjustBy;
        this.paramType = paramType;
        this.descriptorType = descriptorType;
        this.paramId = paramId;
        this.paramName = paramName;
        this.persistenceType = persistenceType;
        this.registerType = registerType;
        this.factoryResetType = factoryResetType;
        this.classNames = classNames;
        this.stringAttributes = stringAttributes;
        this.integerAttributes = integerAttributes;
        this.enumValues = enumValues;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.defaultValue = defaultValue;
        this.scaleBy = scaleBy;
        this.displyPrecision = displyPrecision;
        this.units = units;
    }


    public int getAdjustBy() {
        return adjustBy;
    }

    public String[] getClassNames() {
        return classNames;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescriptorType() {
        return descriptorType;
    }

    public int getDisplyPrecision() {
        return displyPrecision;
    }

    public EnumValue[] getEnumValues() {
        return enumValues;
    }

    public String getFactoryResetType() {
        return factoryResetType;
    }

    public Attribute<Integer>[] getIntegerAttributes() {
        return integerAttributes;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public String getParamId() {
        return paramId;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public String getPersistenceType() {
        return persistenceType;
    }

    public String getRegisterType() {
        return registerType;
    }

    public int getScaleBy() {
        return scaleBy;
    }

    public Attribute<String>[] getStringAttributes() {
        return stringAttributes;
    }

    public String getUnits() {
        return units;
    }

    public class Attribute<T> {
        private final String name;
        private final T value;

        public Attribute(String name, T value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }
    }

    public class EnumValue {
        private final int value;
        private final String text;
        private final String shortText;

        public EnumValue(int value, String text, String shortText) {
            this.shortText = shortText;
            this.value = value;
            this.text = text;
        }
    }

}
