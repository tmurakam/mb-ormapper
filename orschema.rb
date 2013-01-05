#!/usr/bin/env ruby
# -*- coding: utf-8 -*-

=begin
  O/R Mapper library for Android

  Copyright (c) 2010-2011, Takuya Murakami. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1. Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer. 

  2. Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution. 

  3. Neither the name of the project nor the names of its contributors
  may be used to endorse or promote products derived from this software
  without specific prior written permission. 

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
=end

$class_defs = Array.new

def create_table(table, options = {}, &block)
  t = ClassDef.new(table, options)
  block.call(t)
  $class_defs.push t
end

class ClassDef
  attr_accessor :table_name, :base_class_name, :class_name, :members
  attr_accessor :has_many, :has_one, :belongs_to

  def initialize(table, options = {})
    @table_name = table.to_s

    if options[:class]
      @class_name = options[:class].to_s
    else
      @class_name = @table_name.to_s
    end

    if options[:base_class]
      @base_class_name = options[:base_class].to_s
    else
      @base_class_name = @class_name
    end

    @members = Array.new    # members

    # relations
    @has_many = Array.new 
    @has_one = Array.new
    @belongs_to = Array.new
  end

  # attributes
  def integer(name, options = {})
    add_member("INTEGER", name, options)
  end

  def real(name, options = {})
    add_member("REAL", name, options)
  end

  def text(name, options = {})
    add_member("TEXT", name, options)
  end

  def date(name, options = {})
    add_member("DATE", name, options)
  end

  def add_member(type, name, options)
    m = MemberVar.new(type, name, options)
    @members.push(m)
  end

  # relations

  def belongs_to(symbol, options = {})
  end

  def has_many(symbol, options = {})
  end

  def has_one(symbol, options = {})
  end

  def dump
    puts "-- table:#{@table_name}, class:#{@class_name}, base_class:#{@base_class_name} --"
    @members.each do |member|
      member.dump
    end
  end
end

class MemberVar
  attr_reader :type # SQL型名
  attr_reader :field_name # SQLフィールド名
  attr_reader :member_name # メンバ変数名 (mCamelCase)
  attr_reader :getter # ゲッタメソッド名 (camelCase)
  attr_reader :setter # セッタメソッド名 (setCamelCase)
  attr_reader :prop_name # プロパティ名 (getter と同じ)

  def initialize(type, name, options)
    @type = type.to_s
    name = name.to_s

    @getter = camelCase(name)
    @setter = "set" + CamelCase(name)
    @prop_name = @getter

    @member_name = "m" + CamelCase(name)

    if options[:field_name]
      @field_name = options[:field_name].to_s
    else
      @field_name = name
    end

  end

  def dump
    puts "  #{@type} #{@field_name} => #{@prop_name}"
  end

  private
  def camelCase(name)
    name = name.gsub(/_./) { |x| x.gsub(/_/, "").upcase }
    return name
  end 

  def CamelCase(name)
    name = name.gsub(/^./) { |x| x.upcase }
    name = name.gsub(/_./) { |x| x.gsub(/_/, "").upcase }
    return name
  end 
end

class Relation
  attr_reader :name, :class_name, :field_name

  def initialize(name, class_name, field_name)
    @name = name
    @class_name = class_name
    @field_name = field_name
  end
end
             
