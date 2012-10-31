/*
 * Copyright 2012, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.dexlib2.util;

import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.util.ExceptionWithContext;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class InstructionOffsetMap {
    @Nonnull private final int[] instructionCodeOffsets;

    public InstructionOffsetMap(@Nonnull MethodImplementation methodImpl) {
        List<? extends Instruction> instructions = methodImpl.getInstructions();
        int[] instructionCodeOffsets = new int[instructions.size()];

        int codeOffset = 0;
        for (int i=0; i<instructions.size(); i++) {
            instructionCodeOffsets[i] = codeOffset;
            //TODO: handle variable size instructions
            codeOffset += instructions.get(i).getOpcode().format.size / 2;
        }

        this.instructionCodeOffsets = instructionCodeOffsets;
    }

    public int getInstructionIndexAtCodeOffset(int codeOffset) {
        return getInstructionIndexAtCodeOffset(codeOffset, true);
    }

    public int getInstructionIndexAtCodeOffset(int codeOffset, boolean exact) {
        int index = Arrays.binarySearch(instructionCodeOffsets, codeOffset);
        if (index < 0) {
            if (exact) {
                throw new ExceptionWithContext("No instruction at offset %d", codeOffset);
            } else {
                // This calculation would be incorrect if index was -1 (i.e. insertion point of 0). Luckily, we can
                // ignore this case, because codeOffset will always be non-negative, and the code offset of the first
                // instruction will always be 0.
                return (~index) - 1;
            }
        }
        return index;
    }

    public int getInstructionCodeOffset(int index) {
        if (index < 0 || index >= instructionCodeOffsets.length) {
            throw new ExceptionWithContext("Index out of bounds: %d", index);
        }
        return instructionCodeOffsets[index];
    }
}