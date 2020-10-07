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

package org.springframework.test.context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.test.context.BootstrapUtilsTests.OuterClass.NestedWithInheritedBootstrapper;
import org.springframework.test.context.BootstrapUtilsTests.OuterClass.NestedWithInheritedBootstrapper.DoubleNestedWithInheritedButOverriddenBootstrapper;
import org.springframework.test.context.BootstrapUtilsTests.OuterClass.NestedWithInheritedBootstrapper.DoubleNestedWithOverriddenBootstrapper;
import org.springframework.test.context.BootstrapUtilsTests.OuterClass.NestedWithInheritedBootstrapper.DoubleNestedWithOverriddenBootstrapper.TripleNestedWithInheritedBootstrapper;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.web.WebTestContextBootstrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.INHERIT;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.OVERRIDE;

/**
 * Unit tests for {@link BootstrapUtils}.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 4.2
 */
class BootstrapUtilsTests {

	private final CacheAwareContextLoaderDelegate delegate = mock(CacheAwareContextLoaderDelegate.class);

	@Test
	void resolveTestContextBootstrapperWithEmptyBootstrapWithAnnotation() {
		BootstrapContext bootstrapContext = BootstrapTestUtils.buildBootstrapContext(EmptyBootstrapWithAnnotationClass.class, delegate);
		assertThatIllegalStateException().isThrownBy(() ->
				resolveTestContextBootstrapper(bootstrapContext))
			.withMessageContaining("Specify @BootstrapWith's 'value' attribute");
	}

	@Test
	void resolveTestContextBootstrapperWithDoubleMetaBootstrapWithAnnotations() {
		BootstrapContext bootstrapContext = BootstrapTestUtils.buildBootstrapContext(
			DoubleMetaAnnotatedBootstrapWithAnnotationClass.class, delegate);
		assertThatIllegalStateException().isThrownBy(() ->
				resolveTestContextBootstrapper(bootstrapContext))
			.withMessageContaining("Configuration error: found multiple declarations of @BootstrapWith")
			.withMessageContaining(FooBootstrapper.class.getName())
			.withMessageContaining(BarBootstrapper.class.getName());
	}

	@Test
	void resolveTestContextBootstrapperForNonAnnotatedClass() {
		assertBootstrapper(NonAnnotatedClass.class, DefaultTestContextBootstrapper.class);
	}

	@Test
	void resolveTestContextBootstrapperForWebAppConfigurationAnnotatedClass() {
		assertBootstrapper(WebAppConfigurationAnnotatedClass.class, WebTestContextBootstrapper.class);
	}

	@Test
	void resolveTestContextBootstrapperWithDirectBootstrapWithAnnotation() {
		assertBootstrapper(DirectBootstrapWithAnnotationClass.class, FooBootstrapper.class);
	}

	@Test
	void resolveTestContextBootstrapperWithInheritedBootstrapWithAnnotation() {
		assertBootstrapper(InheritedBootstrapWithAnnotationClass.class, FooBootstrapper.class);
	}

	@Test
	void resolveTestContextBootstrapperWithMetaBootstrapWithAnnotation() {
		assertBootstrapper(MetaAnnotatedBootstrapWithAnnotationClass.class, BarBootstrapper.class);
	}

	@Test
	void resolveTestContextBootstrapperWithDuplicatingMetaBootstrapWithAnnotations() {
		assertBootstrapper(DuplicateMetaAnnotatedBootstrapWithAnnotationClass.class, FooBootstrapper.class);
	}

	/**
	 * @since 5.3
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource
	void resolveTestContextBootstrapperInEnclosingClassHierarchy(Class<?> testClass, String name, Class<?> bootstrapper) {
		assertBootstrapper(testClass, bootstrapper);
	}

	static Stream<Arguments> resolveTestContextBootstrapperInEnclosingClassHierarchy() {
		return Stream.of(//
			arguments(OuterClass.class, OuterClass.class.getSimpleName(), FooBootstrapper.class),//
			arguments(NestedWithInheritedBootstrapper.class, NestedWithInheritedBootstrapper.class.getSimpleName(), FooBootstrapper.class),//
			arguments(DoubleNestedWithInheritedButOverriddenBootstrapper.class, DoubleNestedWithInheritedButOverriddenBootstrapper.class.getSimpleName(), EnigmaBootstrapper.class),//
			arguments(DoubleNestedWithOverriddenBootstrapper.class, DoubleNestedWithOverriddenBootstrapper.class.getSimpleName(), BarBootstrapper.class)//
			// TODO Fix bug and include the following.
			// arguments(TripleNestedWithInheritedBootstrapper.class, TripleNestedWithInheritedBootstrapper.class.getSimpleName(), BarBootstrapper.class)//
		);
	}

	/**
	 * @since 5.1
	 */
	@Test
	void resolveTestContextBootstrapperWithLocalDeclarationThatOverridesMetaBootstrapWithAnnotations() {
		assertBootstrapper(LocalDeclarationAndMetaAnnotatedBootstrapWithAnnotationClass.class, EnigmaBootstrapper.class);
	}

	private void assertBootstrapper(Class<?> testClass, Class<?> expectedBootstrapper) {
		BootstrapContext bootstrapContext = BootstrapTestUtils.buildBootstrapContext(testClass, delegate);
		TestContextBootstrapper bootstrapper = resolveTestContextBootstrapper(bootstrapContext);
		assertThat(bootstrapper).isNotNull();
		assertThat(bootstrapper.getClass()).isEqualTo(expectedBootstrapper);
	}

	// -------------------------------------------------------------------

	static class FooBootstrapper extends DefaultTestContextBootstrapper {}

	static class BarBootstrapper extends DefaultTestContextBootstrapper {}

	static class EnigmaBootstrapper extends DefaultTestContextBootstrapper {}

	@BootstrapWith(FooBootstrapper.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface BootWithFoo {}

	@BootstrapWith(FooBootstrapper.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface BootWithFooAgain {}

	@BootstrapWith(BarBootstrapper.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface BootWithBar {}

	// Invalid
	@BootstrapWith
	static class EmptyBootstrapWithAnnotationClass {}

	// Invalid
	@BootWithBar
	@BootWithFoo
	static class DoubleMetaAnnotatedBootstrapWithAnnotationClass {}

	static class NonAnnotatedClass {}

	@BootstrapWith(FooBootstrapper.class)
	static class DirectBootstrapWithAnnotationClass {}

	static class InheritedBootstrapWithAnnotationClass extends DirectBootstrapWithAnnotationClass {}

	@BootWithBar
	static class MetaAnnotatedBootstrapWithAnnotationClass {}

	@BootWithFoo
	@BootWithFooAgain
	static class DuplicateMetaAnnotatedBootstrapWithAnnotationClass {}

	@BootWithFoo
	@BootWithBar
	@BootstrapWith(EnigmaBootstrapper.class)
	static class LocalDeclarationAndMetaAnnotatedBootstrapWithAnnotationClass {}

	@WebAppConfiguration
	static class WebAppConfigurationAnnotatedClass {}

	@BootWithFoo
	static class OuterClass {

		@NestedTestConfiguration(INHERIT)
		class NestedWithInheritedBootstrapper {

			@NestedTestConfiguration(INHERIT)
			@BootstrapWith(EnigmaBootstrapper.class)
			class DoubleNestedWithInheritedButOverriddenBootstrapper {
			}

			@NestedTestConfiguration(OVERRIDE)
			@BootWithBar
			class DoubleNestedWithOverriddenBootstrapper {

				@NestedTestConfiguration(INHERIT)
				class TripleNestedWithInheritedBootstrapper {
				}
			}
		}
	}

}
