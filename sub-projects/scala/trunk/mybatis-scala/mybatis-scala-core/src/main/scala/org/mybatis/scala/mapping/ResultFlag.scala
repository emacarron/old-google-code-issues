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

package org.mybatis.scala.mapping

import org.apache.ibatis.mapping.{ResultFlag => ResultFlagEnum}

sealed trait ResultFlag {
  val unwrap : ResultFlagEnum
}

object ResultFlag {
  val ID = new ResultFlag { val unwrap = ResultFlagEnum.ID }
  val CONSTRUCTOR = new ResultFlag { val unwrap = ResultFlagEnum.CONSTRUCTOR }
}

