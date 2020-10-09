/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.annotation.DirtiesContext.HierarchyMode.CURRENT_LEVEL;
import static org.springframework.test.annotation.DirtiesContext.HierarchyMode.EXHAUSTIVE;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

/**
 * Unit tests for {@link DirtiesContextBeforeModesTestExecutionListener}.
 * and {@link DirtiesContextTestExecutionListener}
 *
 * @author Sam Brannen
 * @since 4.0
 */
@DisplayName("@DirtiesContext TestExecutionListener tests")
class DirtiesContextTestExecutionListenerTests {

	private final TestExecutionListener beforeListener = new DirtiesContextBeforeModesTestExecutionListener();
	private final TestExecutionListener afterListener = new DirtiesContextTestExecutionListener();
	private final TestContext testContext = mock(TestContext.class);


	@Nested
	@DisplayName("Before and after test method")
	class BeforeAndAfterTestMethodTests {

		@Test
		void declaredLocallyOnMethodWithBeforeMethodMode() throws Exception {
			Class<?> clazz = getClass().getEnclosingClass();
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(
				clazz.getDeclaredMethod("dirtiesContextDeclaredLocallyWithBeforeMethodMode"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredLocallyOnMethodWithAfterMethodMode() throws Exception {
			Class<?> clazz = getClass().getEnclosingClass();
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(
				clazz.getDeclaredMethod("dirtiesContextDeclaredLocallyWithAfterMethodMode"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredOnMethodViaMetaAnnotationWithAfterMethodMode() throws Exception {
			Class<?> clazz = getClass().getEnclosingClass();
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(
				clazz.getDeclaredMethod("dirtiesContextDeclaredViaMetaAnnotationWithAfterMethodMode"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredLocallyOnClassBeforeEachTestMethod() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyBeforeEachTestMethod.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("clean"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredLocallyOnClassAfterEachTestMethod() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyAfterEachTestMethod.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("clean"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredViaMetaAnnotationOnClassAfterEachTestMethod() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredViaMetaAnnotationAfterEachTestMethod.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("clean"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredLocallyOnClassBeforeClass() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyBeforeClass.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("clean"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void declaredLocallyOnClassAfterClass() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyAfterClass.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("clean"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void declaredViaMetaAnnotationOnClassAfterClass() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredViaMetaAnnotationAfterClass.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("clean"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void beforeAndAfterTestMethodForDirtiesContextViaMetaAnnotationWithOverrides() throws Exception {
			Class<?> clazz = DirtiesContextViaMetaAnnotationWithOverrides.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("clean"));
			beforeListener.beforeTestMethod(testContext);
			afterListener.beforeTestMethod(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestMethod(testContext);
			beforeListener.afterTestMethod(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(CURRENT_LEVEL);
		}
	}

	@Nested
	@DisplayName("Before and after test class")
	class BeforeAndAfterTestClassTests {

		@Test
		void declaredLocallyOnMethod() throws Exception {
			Class<?> clazz = getClass().getEnclosingClass();
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void declaredLocallyOnClassBeforeEachTestMethod() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyBeforeEachTestMethod.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void declaredLocallyOnClassAfterEachTestMethod() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyAfterEachTestMethod.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void declaredViaMetaAnnotationOnClassAfterEachTestMethod() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredViaMetaAnnotationAfterEachTestMethod.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void declaredLocallyOnClassBeforeClass() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyBeforeClass.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredLocallyOnClassAfterClass() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredLocallyAfterClass.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredViaMetaAnnotationOnClassAfterClass() throws Exception {
			Class<?> clazz = DirtiesContextDeclaredViaMetaAnnotationAfterClass.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}

		@Test
		void declaredViaMetaAnnotationWithOverrides() throws Exception {
			Class<?> clazz = DirtiesContextViaMetaAnnotationWithOverrides.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
		}

		@Test
		void declaredViaMetaAnnotationWithOverriddenAttributes() throws Exception {
			Class<?> clazz = DirtiesContextViaMetaAnnotationWithOverridenAttributes.class;
			BDDMockito.<Class<?>> given(testContext.getTestClass()).willReturn(clazz);
			beforeListener.beforeTestClass(testContext);
			afterListener.beforeTestClass(testContext);
			verify(testContext, times(0)).markApplicationContextDirty(any(HierarchyMode.class));
			afterListener.afterTestClass(testContext);
			beforeListener.afterTestClass(testContext);
			verify(testContext, times(1)).markApplicationContextDirty(EXHAUSTIVE);
		}
	}

	// -------------------------------------------------------------------------

	@DirtiesContext(methodMode = BEFORE_METHOD)
	void dirtiesContextDeclaredLocallyWithBeforeMethodMode() {
		/* no-op */
	}

	@DirtiesContext
	void dirtiesContextDeclaredLocallyWithAfterMethodMode() {
		/* no-op */
	}

	@MetaDirtyAfterMethod
	void dirtiesContextDeclaredViaMetaAnnotationWithAfterMethodMode() {
		/* no-op */
	}


	@DirtiesContext
	@Retention(RetentionPolicy.RUNTIME)
	@interface MetaDirtyAfterMethod {
	}

	@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface MetaDirtyAfterEachTestMethod {
	}

	@DirtiesContext(classMode = AFTER_CLASS)
	@Retention(RetentionPolicy.RUNTIME)
	@interface MetaDirtyAfterClass {
	}

	@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
	static class DirtiesContextDeclaredLocallyBeforeEachTestMethod {

		void clean() {
			/* no-op */
		}
	}

	@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
	static class DirtiesContextDeclaredLocallyAfterEachTestMethod {

		void clean() {
			/* no-op */
		}
	}

	@DirtiesContext
	@Retention(RetentionPolicy.RUNTIME)
	@interface MetaDirtyWithOverrides {

		ClassMode classMode() default AFTER_EACH_TEST_METHOD;

		HierarchyMode hierarchyMode() default HierarchyMode.CURRENT_LEVEL;
	}

	@MetaDirtyAfterEachTestMethod
	static class DirtiesContextDeclaredViaMetaAnnotationAfterEachTestMethod {

		void clean() {
			/* no-op */
		}
	}

	@DirtiesContext(classMode = BEFORE_CLASS)
	static class DirtiesContextDeclaredLocallyBeforeClass {

		void clean() {
			/* no-op */
		}
	}

	@DirtiesContext(classMode = AFTER_CLASS)
	static class DirtiesContextDeclaredLocallyAfterClass {

		void clean() {
			/* no-op */
		}
	}

	@MetaDirtyAfterClass
	static class DirtiesContextDeclaredViaMetaAnnotationAfterClass {

		void clean() {
			/* no-op */
		}
	}

	@MetaDirtyWithOverrides
	static class DirtiesContextViaMetaAnnotationWithOverrides {

		void clean() {
			/* no-op */
		}
	}

	@MetaDirtyWithOverrides(classMode = AFTER_CLASS, hierarchyMode = EXHAUSTIVE)
	static class DirtiesContextViaMetaAnnotationWithOverridenAttributes {

		void clean() {
			/* no-op */
		}
	}

}
