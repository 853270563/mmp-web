<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${table.className}">
<#macro result4Map javaName jdbcName jdbcType >
        <result property="${javaName}" column="${jdbcName}" jdbcType="${jdbcType}" />
</#macro>

    <sql id="BaseColumnList">
    <#assign i = 0 />
        <#list table.fieldList as tf><#if i != 0>,</#if><#assign i = i + 1/><#if i == 6><#assign i = 1/>
        </#if>${tf.collumnName}</#list>
    </sql>

    <sql id="BaseColumnValueList">
    <#assign i = 0 />
        <#list table.fieldList as tf><#if i != 0>,</#if><#assign i = i + 1/><#if i == 6><#assign i = 1/>
        </#if>${"#\{"}${tf.collumnName}${"}"}</#list>
    </sql>

    <sql id="TableClause"> ${"$\{"}${table.schemaPropName}${"}"}.${table.name} </sql>

    <sql id="WhereClause4Id">
        <where>
        <#if table.primaryField?? >
            ${table.primaryField.collumnName} = ${"#\{"}${table.primaryField.collumnName}${"}"}
        </#if>
        </where>
    </sql>

    <insert id="insert" parameterType="map">
        insert into <include refid="TableClause" /> (
        <include refid="BaseColumnList" />
        ) values (
        <include refid="BaseColumnValueList" />
        )
    </insert>

    <select id="queryById" parameterType="string" resultType="map">
        <include refid="public.SelectById" />
    </select>

    <update id="updateById" parameterType="map">
    <#assign i = 0 />
        update <include refid="TableClause" />
        <set><#list table.fieldListWithoutKey as tf><#if i != 0>,</#if><#assign i = i + 1/>
        ${tf.collumnName}=${"#\{"}${tf.collumnName}${"}"}</#list>
        </set> <include refid="WhereClause4Id" />
    </update>

     <update id="updateByIdSelective" parameterType="map">
        <#assign i = 0 />
            update <include refid="TableClause" />
            <set><#list table.fieldListWithoutKey as tf>
                <if test="${tf.collumnName}!=null and ${tf.collumnName}!=''">${tf.collumnName}=${"#\{"}${tf.collumnName}${"}"}<#if tf_has_next>,</#if></if></#list>
            </set> <include refid="WhereClause4Id" />
     </update>


    <update id="deleteById" parameterType="string">
        <include refid="public.DeleteById" />
    </update>

    <select id="queryByCriteria" resultType="map" parameterType="map">
        <include refid="public.Select4Query" />
    </select>

    <select id="countByCriteria" resultType="int" parameterType="map">
        <include refid="public.Count4Query" />
    </select>

    <select id="deleteByCriteria" resultType="int" parameterType="map">
        <include refid="public.Delete4Query" />
    </select>
    
    <select id="queryByMap" resultType="map" parameterType="map">
     select  <include refid="BaseColumnList" />
     from    <include refid="TableClause" />
     <where>
     	<#list table.fieldList as tf>
                <if test="${tf.collumnName}!=null and ${tf.collumnName}!=''"><#if tf_has_next> and</#if> ${tf.collumnName}=${"#\{"}${tf.collumnName}${"}"}</if>
                </#list>
     </where>
    </select>
</mapper>
