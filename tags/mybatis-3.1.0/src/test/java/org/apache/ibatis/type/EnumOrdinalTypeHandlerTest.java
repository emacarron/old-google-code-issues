/*
 *    Copyright 2009-2012 The MyBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

import org.jmock.Expectations;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EnumOrdinalTypeHandlerTest extends BaseTypeHandlerTest {
  
  enum MyEnum {
    ONE, TWO
  }

  private static final TypeHandler<MyEnum> TYPE_HANDLER = new EnumOrdinalTypeHandler<MyEnum>(MyEnum.class);

  @Test
  public void shouldSetParameter() throws Exception {
    mockery.checking(new Expectations() {
      {
        one(ps).setInt(with(any(int.class)), with(0));
      }
    });
    TYPE_HANDLER.setParameter(ps, 1, MyEnum.ONE, null);
    mockery.assertIsSatisfied();
  }

  @Test
  public void shouldSetNullParameter() throws Exception {
    mockery.checking(new Expectations() {
      {
        one(ps).setNull(with(any(int.class)), with(any(int.class)));
      }
    });
    TYPE_HANDLER.setParameter(ps, 1, null, JdbcType.VARCHAR);
    mockery.assertIsSatisfied();
  }

  @Test
  public void shouldGetResultFromResultSet() throws Exception {
    mockery.checking(new Expectations() {
      {
        one(rs).getInt(with(any(String.class)));
        will(returnValue(0));
        one(rs).wasNull();
        will(returnValue(false));
        one(rs).wasNull();
        will(returnValue(false));
      }
    });
    assertEquals(MyEnum.ONE, TYPE_HANDLER.getResult(rs, "column"));
    mockery.assertIsSatisfied();
  }

  @Test
  public void shouldGetNullResultFromResultSet() throws Exception {
    mockery.checking(new Expectations() {
      {
        one(rs).getInt(with(any(String.class)));
        will(returnValue(0));
        one(rs).wasNull();
        will(returnValue(true));
        one(rs).wasNull();
        will(returnValue(true));
      }
    });
    assertEquals(null, TYPE_HANDLER.getResult(rs, "column"));
    mockery.assertIsSatisfied();
  }

  @Test
  public void shouldGetResultFromCallableStatement() throws Exception {
    mockery.checking(new Expectations() {
      {
        one(cs).getInt(with(any(int.class)));
        will(returnValue(0));
        one(cs).wasNull();
        will(returnValue(false));
        one(cs).wasNull();
        will(returnValue(false));
      }
    });
    assertEquals(MyEnum.ONE, TYPE_HANDLER.getResult(cs, 1));
    mockery.assertIsSatisfied();
  }

  @Test
  public void shouldGetNullResultFromCallableStatement() throws Exception {
    mockery.checking(new Expectations() {
      {
        one(cs).getInt(with(any(int.class)));
        will(returnValue(0));
        one(cs).wasNull();
        will(returnValue(true));
        one(cs).wasNull();
        will(returnValue(true));
      }
    });
    assertEquals(null, TYPE_HANDLER.getResult(cs, 1));
    mockery.assertIsSatisfied();
  }

}
