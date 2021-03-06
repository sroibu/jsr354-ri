/*
  Copyright (c) 2012, 2020, Anatole Tresch, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.function;

import java.util.Map;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

/**
 * Internal class implementing grouped statistic functionality.
 */
public class GroupMonetarySummaryStatistics {

	private final Map<CurrencyUnit, MonetarySummaryStatistics> groupSummary = new MonetarySummaryMap();

    GroupMonetarySummaryStatistics() {

    }

	public Map<CurrencyUnit, MonetarySummaryStatistics> get() {
        return groupSummary;
    }

    public GroupMonetarySummaryStatistics accept(MonetaryAmount amount) {
        CurrencyUnit currency = Objects.requireNonNull(amount).getCurrency();
        groupSummary.putIfAbsent(currency, new DefaultMonetarySummaryStatistics(
                currency));
		MonetarySummaryStatistics summary = groupSummary.get(currency);
        summary.accept(amount);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupSummary);
    }

    @Override
    public boolean equals(Object obj) {
        if (GroupMonetarySummaryStatistics.class.isInstance(obj)) {
            GroupMonetarySummaryStatistics other = GroupMonetarySummaryStatistics.class
                    .cast(obj);
            return Objects.equals(groupSummary, other.groupSummary);
        }
        return false;
    }

    @Override
    public String toString() {
        return "GroupMonetarySummaryStatistics: " + groupSummary.toString();
    }

    public GroupMonetarySummaryStatistics combine(
            GroupMonetarySummaryStatistics another) {
        Objects.requireNonNull(another);

        for (CurrencyUnit keyCurrency : another.groupSummary.keySet()) {
            groupSummary.putIfAbsent(keyCurrency,
                    new DefaultMonetarySummaryStatistics(keyCurrency));
			groupSummary.merge(keyCurrency,
					another.groupSummary.get(keyCurrency),
					MonetarySummaryStatistics::combine);
        }
        return this;
    }

}
