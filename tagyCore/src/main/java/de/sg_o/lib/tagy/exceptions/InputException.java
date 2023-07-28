/*
 *
 *      Copyright (C) 2023 Joerg Bayer (SG-O)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.sg_o.lib.tagy.exceptions;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Objects;

public class InputException extends Exception {
    @SuppressWarnings("unused")
    public enum Rule {
        INVALID_MANDATORY_FIELD("The data entered into the mandatory field {0} is not valid. Please check your input."),
        EMPTY_MANDATORY_FIELD("The mandatory field {0} is empty. Please check your input."),
        LESS_THAN_MIN("The data entered in field {0} is smaller than the minimum allowed value {1}. Please check your input."),
        MORE_THAN_MAX("The data entered in field {0} is larger than the maximum allowed value {1}. Please check your input."),
        LIST_NOT_ENOUGH_VALUES("The {0} list has less than the required {1} values. Please check your input"),
        LIST_TOO_MANY_VALUES("The {0} list has more than the allowed {1} values. Please check your input."),
        STRING_TOO_SHORT("The data entered in field {0} is shorter than the minimum required length {1}. Please check your input"),
        STRING_TOO_LONG("The data entered in field {0} is longer than the maximum allowed length {1}. Please check your input."),
        OTHER("Data entered in field {0} is not valid. Please check your input.");

        private final String message;

        Rule(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @NotNull
    private final Rule rule;
    @NotNull
    private final String tag;
    private double value = Double.NaN;

    @SuppressWarnings("NullableProblems")
    public InputException(@NotNull Rule rule, String tag) {
        this.rule = rule;
        if (tag == null) tag = "";
        this.tag = tag;
    }

    public InputException(@NotNull Rule rule, String tag, double value) {
        this(rule, tag);
        this.value = value;
    }

    public @NotNull Rule getRule() {
        return rule;
    }

    public @NotNull String getTag() {
        return tag;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputException that = (InputException) o;
        return Double.compare(that.getValue(), getValue()) == 0 && getRule() == that.getRule() && Objects.equals(getTag(), that.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRule(), getTag(), getValue());
    }

    @Override
    public String toString() {
        return MessageFormat.format(rule.getMessage(), tag, value);
    }
}
