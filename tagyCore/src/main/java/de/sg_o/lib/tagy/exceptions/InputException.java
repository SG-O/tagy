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

import static de.sg_o.lib.tagy.util.MessageLoader.getMessageFromBundle;

public class InputException extends Exception {
    @SuppressWarnings("unused")
    public enum Rule {
        INVALID_MANDATORY_FIELD(getMessageFromBundle("translations/text", "exception.rule.invalidMandatoryField")),
        EMPTY_MANDATORY_FIELD(getMessageFromBundle("translations/text", "exception.rule.emptyMandatoryField")),
        LESS_THAN_MIN(getMessageFromBundle("translations/text", "exception.rule.lessTanMin")),
        MORE_THAN_MAX(getMessageFromBundle("translations/text", "exception.rule.moreThanMax")),
        LIST_NOT_ENOUGH_VALUES(getMessageFromBundle("translations/text", "exception.rule.listNotEnoughValues")),
        LIST_TOO_MANY_VALUES(getMessageFromBundle("translations/text", "exception.rule.listTooManyValues")),
        STRING_TOO_SHORT(getMessageFromBundle("translations/text", "exception.rule.stringTooShort")),
        STRING_TOO_LONG(getMessageFromBundle("translations/text", "exception.rule.stringTooLong")),
        OTHER(getMessageFromBundle("translations/text", "exception.rule.other"));

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
