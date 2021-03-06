package org.infinispan.factories;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.Configurations;
import org.infinispan.container.versioning.NumericVersionGenerator;
import org.infinispan.container.versioning.SimpleClusteredVersionGenerator;
import org.infinispan.container.versioning.VersionGenerator;
import org.infinispan.factories.annotations.DefaultFactoryFor;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.factories.scopes.Scope;
import org.infinispan.factories.scopes.Scopes;

/**
 * Version generator component factory. Version generators are used for situations where version or ids are needed, e.g.
 * data versioning, transaction recovery, or hotrod/memcached support.
 *
 * @author Manik Surtani
 * @author Galder Zamarreño
 * @since 5.1
 */
@DefaultFactoryFor(classes = VersionGenerator.class)
@Scope(Scopes.NAMED_CACHE)
public class VersionGeneratorFactory extends NamedComponentFactory implements AutoInstantiableFactory {

   private Configuration configuration;

   @Override
   public <T> T construct(Class<T> componentType, String componentName) {
      if (KnownComponentNames.TRANSACTION_VERSION_GENERATOR.endsWith(componentName)) {
         return componentType.cast(new NumericVersionGenerator());
      }
      if (Configurations.isTxVersioned(configuration)) {
         return configuration.clustering().cacheMode().isClustered() ?
               componentType.cast(new SimpleClusteredVersionGenerator()) :
               componentType.cast(new NumericVersionGenerator());
      } else {
         return componentType.cast(new NumericVersionGenerator());
      }
   }

   @Inject
   private void injectGlobalDependencies(Configuration configuration) {
      this.configuration = configuration;
   }

}
