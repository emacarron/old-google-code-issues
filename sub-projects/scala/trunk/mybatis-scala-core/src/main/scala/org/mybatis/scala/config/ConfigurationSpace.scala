/*
 * Copyright 2011 The myBatis Team
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

package org.mybatis.scala.config

import org.apache.ibatis.session.{Configuration => MBConfig}
import org.apache.ibatis.executor.keygen.{Jdbc3KeyGenerator, NoKeyGenerator, SelectKeyGenerator, KeyGenerator => MBKeyGenerator}
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.mybatis.scala.mapping._
import java.util.ArrayList
import org.apache.ibatis.mapping.{ResultMapping => MBResultMapping, SqlSource, SqlCommandType, Discriminator}

/** Configuration Space (mybatis namespace)
  * @constructor Creates an empty configuration space.
  * @param configuration myBatis Configuration target
  * @param spaceName Space name or namespace
  * @version \$Revision$
  */
class ConfigurationSpace(configuration : MBConfig, spaceName : String = "_DEFAULT_") {

  // == Start primary constructor code ===

  private val builderAssistant = new MapperBuilderAssistant(configuration, spaceName)

  builderAssistant.setCurrentNamespace(spaceName)

  // == End Primary constructor code ===

  // == Start of public API ===

  /** Adds a statement to the space */
  def += (s : Statement) : this.type = addStatement(s)

  /** Adds a sequence of statements to the space */
  def ++=(ss : Seq[Statement]) : this.type = {
    for (s <- ss) addStatement(s)
    this
  }

  /** Adds a mapper to the space */
  def ++=(mapper : { def bind : Seq[Statement] }) : this.type = ++=(mapper.bind)

  // == End of public API ===

  private def addResultMap(rm : ResultMap[_]) : Unit = {
    if (rm.fqi == null) {
      rm.fqi = ConfigurationSpace.generateFQI(spaceName, rm)
      if (rm.parent != null) addResultMap(rm.parent)
      val resultMappings = new ArrayList[MBResultMapping]

      // Mappings
      for (r <- rm.constructor ++ rm.mappings) {
        if (r.nestedSelect != null) addStatement(r.nestedSelect)
        if (r.nestedResultMap != null) addResultMap(r.nestedResultMap)
        resultMappings add
          builderAssistant.buildResultMapping(
            r.resultTypeClass,
            r.property,
            r.column,
            r.javaTypeClass,
            r.jdbcTypeEnum,
            resolveFQI(r.nestedSelect),
            resolveFQI(r.nestedResultMap),
            r.notNullColumn,
            r.typeHandlerClass,
            r.flags
          )
      }

      // Discriminator
      import java.util.HashMap
      var discriminator : Discriminator = null
      rm.discr match {
        case (column, javaType, jdbcType, typeHandler, cases) =>
          val discriminatorMap = new HashMap[String,String]
          for (c <- cases) {
            addResultMap(c.resultMap)
            discriminatorMap.put(c.value, c.resultMap.fqi.resolveIn(spaceName))
          }
          discriminator = builderAssistant.buildDiscriminator(
            rm.resultTypeClass,
            column,
            if (javaType == null || javaType.isVoid) classOf[String] else javaType.unwrap,
            if (jdbcType == null || jdbcType == JdbcType.UNDEFINED) null else jdbcType.unwrap,
            if (typeHandler == null) null else typeHandler.unwrap,
            discriminatorMap
          )
        case _ =>
          // Skip
      }

      // Assemble
      builderAssistant.addResultMap(
        rm.fqi.id,
        rm.resultTypeClass,
        if (rm.parent != null) rm.parent.fqi.id else null,
        discriminator,
        resultMappings
      )

    }
  }

  private def resolveFQI(r : { val fqi : FQI}) : String = {
    if (r == null) null else r.fqi resolveIn spaceName
  }

  private def addStatement(statement : Statement) : this.type = {
    if (statement.fqi == null) {
      statement.fqi = ConfigurationSpace.generateFQI(spaceName, statement)
      statement match {
        case stmt : Select =>
          if (stmt.resultMap != null) addResultMap(stmt.resultMap)
          builderAssistant.addMappedStatement(
            stmt.fqi.resolveIn(spaceName),
            buildDynamicSQL(stmt.xsql),
            stmt.statementType.unwrap,
            SqlCommandType.SELECT,
            if (stmt.fetchSize > 0) stmt.fetchSize else null,
            if (stmt.timeout > -1) stmt.timeout else null,
            null,
            stmt.parameterTypeClass,
            resolveFQI(stmt.resultMap),
            stmt.resultTypeClass,
            stmt.resultSetType.unwrap,
            stmt.flushCache,
            stmt.useCache,
            new NoKeyGenerator(),
            null
          )
        case stmt : Insert[_] =>
          builderAssistant.addMappedStatement(
            stmt.fqi.resolveIn(spaceName),
            buildDynamicSQL(stmt.xsql),
            stmt.statementType.unwrap,
            SqlCommandType.INSERT,
            null,
            if (stmt.timeout > -1) stmt.timeout else null,
            null,
            stmt.parameterTypeClass,
            null,
            classOf[Int],
            ResultSetType.FORWARD_ONLY.unwrap,
            stmt.flushCache,
            false,
            buildKeyGenerator(stmt.keyGenerator, stmt.parameterTypeClass, stmt.fqi.id),
            if (stmt.keyGenerator == null) null else stmt.keyGenerator.keyProperty
          )
        case stmt : Update[_] =>
          builderAssistant.addMappedStatement(
            stmt.fqi.resolveIn(spaceName),
            buildDynamicSQL(stmt.xsql),
            stmt.statementType.unwrap,
            SqlCommandType.UPDATE,
            null,
            if (stmt.timeout > -1) stmt.timeout else null,
            null,
            stmt.parameterTypeClass,
            null,
            classOf[Int],
            ResultSetType.FORWARD_ONLY.unwrap,
            stmt.flushCache,
            false,
            new NoKeyGenerator(),
            null
          )
        case stmt : Delete[_] =>
          builderAssistant.addMappedStatement(
            stmt.fqi.resolveIn(spaceName),
            buildDynamicSQL(stmt.xsql),
            stmt.statementType.unwrap,
            SqlCommandType.DELETE,
            null,
            if (stmt.timeout > -1) stmt.timeout else null,
            null,
            stmt.parameterTypeClass,
            null,
            classOf[Int],
            ResultSetType.FORWARD_ONLY.unwrap,
            stmt.flushCache,
            false,
            new NoKeyGenerator(),
            null
          )
        case unsupported =>
          error("Unsupported statement type")
      }
    }
    this
  }

  private def buildDynamicSQL(xsql : XSQL) : SqlSource
    = new DynamicSQLBuilder(configuration, xsql).build

  private def buildKeyGenerator(generator : KeyGenerator, parameterTypeClass : Class[_], baseId : String) : MBKeyGenerator = {
    generator match {
      case jdbc : JdbcGeneratedKey =>
        new Jdbc3KeyGenerator(jdbc.keyColumn)
      case sql : SqlGeneratedKey[_] =>
        buildSqlKeyGenerator(sql, parameterTypeClass, baseId)
      case _ =>
        new NoKeyGenerator()
    }
  }

  private def buildSqlKeyGenerator(generator : SqlGeneratedKey[_], parameterTypeClass : Class[_], baseId : String) : MBKeyGenerator = {

    val id = baseId + SelectKeyGenerator.SELECT_KEY_SUFFIX
    val useCache = false
    val keyGenerator = new NoKeyGenerator()
    val fetchSize = null
    val timeout = null
    val flushCache = false
    val parameterMap = null
    val resultMap = null
    val resultSetTypeEnum = null
    val sqlSource = buildDynamicSQL(generator.xsql)
    val sqlCommandType = SqlCommandType.SELECT
    val statementType = generator.statementType.unwrap
    val keyProperty = generator.keyProperty
    val executeBefore = generator.executeBefore
    val resultTypeClass = generator.resultTypeClass

    builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
      fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
      resultSetTypeEnum, flushCache, useCache, keyGenerator, keyProperty)

    val keyStatement = configuration.getMappedStatement(id, false)
    val answer = new SelectKeyGenerator(keyStatement, executeBefore)

    configuration.addKeyGenerator(id, answer)
    answer
  }

}

private object ConfigurationSpace {

  private var count : Int = 0

  private[config] def generateFQI(spaceId : String, subject : AnyRef) = {
    count += 1
    FQI(spaceId, subject.getClass.getName.replace('.', '-') + "-" + count)
  }

}
