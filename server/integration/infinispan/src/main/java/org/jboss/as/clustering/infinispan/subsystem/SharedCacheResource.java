/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.clustering.infinispan.subsystem;

import org.infinispan.partitionhandling.AvailabilityMode;
import org.jboss.as.controller.OperationDefinition;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleOperationDefinitionBuilder;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.operations.validation.EnumValidator;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.services.path.ResolvePathHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * Base class for cache resources which require common cache attributes, clustered cache attributes
 * and shared cache attributes.
 *
 * @author Richard Achmatowicz (c) 2011 Red Hat Inc.
 */
public class SharedCacheResource extends ClusteredCacheResource {

   static final SimpleAttributeDefinition CACHE_AVAILABILITY =
         new SimpleAttributeDefinitionBuilder(ModelKeys.CACHE_AVAILABILITY, ModelType.STRING, true)
                 .setFlags(AttributeAccess.Flag.STORAGE_RUNTIME)
                 .setValidator(new EnumValidator<>(AvailabilityMode.class, false, false))
                 .build();

   static final SimpleAttributeDefinition CACHE_REBALANCE =
           new SimpleAttributeDefinitionBuilder(ModelKeys.CACHE_REBALANCE, ModelType.BOOLEAN, true)
                   .setFlags(AttributeAccess.Flag.STORAGE_RUNTIME)
                   .build();

   static final SimpleAttributeDefinition CACHE_REBALANCING_STATUS =
           new SimpleAttributeDefinitionBuilder(ModelKeys.CACHE_REBALANCING_STATUS, ModelType.STRING, false)
                   .setFlags(AttributeAccess.Flag.STORAGE_RUNTIME)
                   .build();

   static final SimpleAttributeDefinition BOOL_VALUE = SimpleAttributeDefinitionBuilder.create(ModelKeys.VALUE, ModelType.BOOLEAN, false)
         .setDefaultValue(new ModelNode(true))
         .build();

   static final OperationDefinition CACHE_REBALANCE_OPERATION = new SimpleOperationDefinitionBuilder(ModelKeys.CACHE_REBALANCE, new InfinispanResourceDescriptionResolver("clustered-cache"))
         .setParameters(BOOL_VALUE)
         .setRuntimeOnly()
         .build();

    public SharedCacheResource(PathElement pathElement, ResourceDescriptionResolver descriptionResolver, CacheAdd addHandler, OperationStepHandler removeHandler, ResolvePathHandler resolvePathHandler, boolean runtimeRegistration) {
        super(pathElement, descriptionResolver, addHandler, removeHandler, resolvePathHandler, runtimeRegistration);
    }

   @Override
   public void registerOperations(ManagementResourceRegistration resourceRegistration) {
      super.registerOperations(resourceRegistration);
      resourceRegistration.registerOperationHandler(CACHE_REBALANCE_OPERATION, CacheCommands.CacheRebalanceCommand.INSTANCE);
   }

   @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        super.registerAttributes(resourceRegistration);
        if (runtimeRegistration) {
            resourceRegistration.registerReadWriteAttribute(CACHE_AVAILABILITY, CacheAvailabilityAttributeHandler.INSTANCE, CacheAvailabilityAttributeHandler.INSTANCE);
            resourceRegistration.registerReadWriteAttribute(CACHE_REBALANCE, CacheRebalanceAttributeHandler.INSTANCE, CacheRebalanceAttributeHandler.INSTANCE);
            resourceRegistration.registerReadWriteAttribute(CACHE_REBALANCING_STATUS, CacheRebalancingStatusAttributeHandler.INSTANCE, CacheRebalancingStatusAttributeHandler.INSTANCE);
        }
    }
}
