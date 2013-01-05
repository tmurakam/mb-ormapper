#!/usr/bin/env ruby

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

class ClassDef
  attr_accessor :tableName, :baseClassName, :className, :members
  attr_accessor :has_many, :has_one, :belongs_to

  def initialize
    @tableName = nil        # table name
    @baseClassName = nil    # base class name
    @className = nil        # real class name
    @members = Array.new    # members

    # relations
    @has_many = Array.new 
    @has_one = Array.new
    @belongs_to = Array.new
  end

  def dump
    puts "-- #{@name} --"
    @members.each do |member|
      member.dump
    end
  end
end

class MemberVar
  attr_reader :type, :fieldName, :memberName
  attr_reader :getter, :setter, :propName

  def initialize(type, name, fieldName = nil)
    @type = type

    @getter = camelCase(name)
    @setter = "set" + CamelCase(name)
    @propName = @getter

    @memberName = "m" + CamelCase(name)

    if (fieldName != nil)
      @fieldName = fieldName
    else
      @fieldName = name
    end

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

  def dump
    puts "  #{@type}: #{@fieldName} => #{@propertyName}"
  end
end

class Relation
  attr_reader :name, :className

  def initialize(name, className)
    @name = name
    @className = className
  end
end
                 
class Schema
  attr_reader :defs
  attr_reader :vers

  def initialize
    @defs = Array.new
    @vers = Hash.new
  end

  def loadFromFile(filename)
    open(filename) do |fh|
      
      classdef = nil

      fh.each do |line|
        line.chop!

        # remove comment
        line.gsub!(/#.*$/, "")

        if (line =~ /^\s*$/)
          # empty line
          next                   

        elsif (line =~ /^(\S+)\s*=\s*(\S+)/)
          # variable def.
          name = $1
          value = $2
          @vers[name] = value

        elsif (line =~ /^\S/)
          # start class def
          tableName = baseClassName = className = nil
          if (line =~ /(.*)\s*:\s*(.*)\s*,\s*(.*)/)
            tableName = $1
            className = $2
            baseClassName = $3
          elsif (line =~ /(.*)\s*:\s*(.*)/)
            tableName = $1
            className = $2
            baseClassName = className
          else
            line =~ /^(\S+):?/
            tableName = $1
            className = name
            baseClassName = name
          end

          if (classdef != nil)
            @defs.push(classdef)
          end
          classdef = ClassDef.new
          classdef.tableName = tableName
          classdef.className = className
          classdef.baseClassName = baseClassName

        elsif (line =~ /\s+(\S+)\s*=>\s*(\S+)\s*:\s*(\S+)/)
          # fieldName => column: type
          member = MemberVar.new($3, $1, $2)
          classdef.members.push(member)
          
        elsif (line =~ /\s+(\S+)\s*:\s*(\S+)\s*(\S+)/)
          # column: has_many/has_one/belongs_to class
          has_many = Relation.new($1, $3)
          case $2
          when "has_many"
            classdef.has_many.push(has_many)
          when "has_one"
            classdef.has_one.push(has_many)
          when "belongs_to"
            classdef.belongs_to.push(has_many)
          end

        elsif (line =~ /\s+(\S+)\s*:\s*(\S+)/)
          # column: type
          member = MemberVar.new($2, $1)
          classdef.members.push(member)
        end
      end
      if (classdef != nil)
        @defs.push(classdef)
      end
    end
  end

  def dump
    @defs.each do |classdef|
      classdef.dump
    end
  end
end

