package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "JPush 푸시 서비스 모듈", packageName = "com.x.jpush.assemble.control", containerEntities = {
		"com.x.jpush.core.entity.PushDevice" }, storeJars = { "x_organization_core_entity",
				"x_organization_core_express", "x_jpush_core_entity" })
public class x_jpush_assemble_control extends Deployable {
}
