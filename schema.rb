#!/usr/bin/ruby

=begin
  O/R Mapper library for iPhone

  Copyright (c) 2010, Takuya Murakami. All rights reserved.

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
    attr_accessor :name, :bcname, :members, :types

    def initialize
        @name = nil
        @bcname = nil
        @members = Array.new
        @types = Hash.new
    end

    def dump
        puts "-- #{@name} --"
        @members.each do |member|
            puts "  #{@types[member]}: #{member}"
        end
    end
end

class Schema
    attr_reader :defs

    def initialize
        @defs = Array.new
    end

    def loadFromFile(filename)
        open(filename) do |fh|
            
            classdef = nil

            fh.each do |line|
                line.chop!
                
                if (line =~ /^\S/)
                    name = bcname = nil
                    if (line =~ /(.*)\s*:\s*(.*)/)
                        name = $1
                        bcname = $2
                    else
                        line =~ /^(\S+)/
                        name = $1
                        bcname = name
                    end

                    if (classdef != nil)
                        @defs.push(classdef)
                    end
                    classdef = ClassDef.new
                    classdef.name = name
                    classdef.bcname = bcname
                elsif (line =~ /\s+(\S+)\s*:(\S+)/)
                    member = $1
                    type = $2
                    classdef.members.push(member)
                    classdef.types[member] = type
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

        
