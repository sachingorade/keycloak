/*
 * Copyright 2022. Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.models.map.storage.ldap;

import org.keycloak.models.map.common.AbstractEntity;
import org.keycloak.models.map.common.StringKeyConvertor;
import org.keycloak.models.map.storage.chm.MapModelCriteriaBuilder;
import org.keycloak.storage.SearchableModelField;

import java.util.Map;
import java.util.function.Predicate;

public class MapModelCriteriaBuilderAssumingEqualForField<K, V extends AbstractEntity, M> extends MapModelCriteriaBuilder<K, V, M> {

    private final Map<SearchableModelField<? super M>, UpdatePredicatesFunc<K, V, M>> fieldPredicates;
    private final StringKeyConvertor<K> keyConvertor;
    private final SearchableModelField<? super M> modelFieldThatShouldCompareToTrueForEqual;

    public MapModelCriteriaBuilderAssumingEqualForField(StringKeyConvertor<K> keyConvertor, Map<SearchableModelField<? super M>, UpdatePredicatesFunc<K, V, M>> fieldPredicates, SearchableModelField<? super M> modelFieldThatShouldCompareToTrueForEqual) {
        this(keyConvertor, fieldPredicates, ALWAYS_TRUE, ALWAYS_TRUE, modelFieldThatShouldCompareToTrueForEqual);
    }

    protected MapModelCriteriaBuilderAssumingEqualForField(StringKeyConvertor<K> keyConvertor, Map<SearchableModelField<? super M>, UpdatePredicatesFunc<K, V, M>> fieldPredicates, Predicate<? super K> indexReadFilter, Predicate<? super V> sequentialReadFilter, SearchableModelField<? super M> modelFieldThatShouldCompareToTrueForEqual) {
        super(keyConvertor, fieldPredicates, indexReadFilter, sequentialReadFilter);
        this.keyConvertor = keyConvertor;
        this.modelFieldThatShouldCompareToTrueForEqual = modelFieldThatShouldCompareToTrueForEqual;
        this.fieldPredicates = fieldPredicates;
    }

    @Override
    public MapModelCriteriaBuilder<K, V, M> compare(SearchableModelField<? super M> modelField, Operator op, Object... values) {
        if (modelField == modelFieldThatShouldCompareToTrueForEqual && op == Operator.EQ) {
            return instantiateNewInstance(
                    keyConvertor,
                    fieldPredicates,
                    ALWAYS_TRUE,
                    ALWAYS_TRUE);
        }
        return super.compare(modelField, op, values);
    }

    @Override
    protected MapModelCriteriaBuilder<K, V, M> instantiateNewInstance(StringKeyConvertor<K> keyConvertor, Map<SearchableModelField<? super M>, UpdatePredicatesFunc<K, V, M>> fieldPredicates, Predicate<? super K> indexReadFilter, Predicate<? super V> sequentialReadFilter) {
        return new MapModelCriteriaBuilderAssumingEqualForField<>(keyConvertor, fieldPredicates, indexReadFilter, sequentialReadFilter, modelFieldThatShouldCompareToTrueForEqual);
    }
}
