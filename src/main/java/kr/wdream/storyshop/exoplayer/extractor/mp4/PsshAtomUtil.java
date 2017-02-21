/*
 * Copyright (C) 2014 The Android Open Source Project
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
package kr.wdream.storyshop.exoplayer.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import kr.wdream.storyshop.exoplayer.util.ParsableByteArray;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Utility methods for handling PSSH atoms.
 */
public final class PsshAtomUtil {

  private static final String TAG = "PsshAtomUtil";

  private PsshAtomUtil() {}

  /**
   * Builds a PSSH atom for a given {@link UUID} containing the given scheme specific data.
   *
   * @param uuid The UUID of the scheme.
   * @param data The scheme specific data.
   * @return The PSSH atom.
   */
  public static byte[] buildPsshAtom(UUID uuid, byte[] data) {
    int psshBoxLength = Atom.FULL_HEADER_SIZE + 16 /* UUID */ + 4 /* DataSize */ + data.length;
    ByteBuffer psshBox = ByteBuffer.allocate(psshBoxLength);
    psshBox.putInt(psshBoxLength);
    psshBox.putInt(Atom.TYPE_pssh);
    psshBox.putInt(0 /* version=0, flags=0 */);
    psshBox.putLong(uuid.getMostSignificantBits());
    psshBox.putLong(uuid.getLeastSignificantBits());
    psshBox.putInt(data.length);
    psshBox.put(data);
    return psshBox.array();
  }

  /**
   * Parses the UUID from a PSSH atom. Version 0 and 1 PSSH atoms are supported.
   * <p>
   * The UUID is only parsed if the data is a valid PSSH atom.
   *
   * @param atom The atom to parse.
   * @return The parsed UUID. Null if the input is not a valid PSSH atom, or if the PSSH atom has
   *     an unsupported version.
   */
  public static UUID parseUuid(byte[] atom) {
    Pair<UUID, byte[]> parsedAtom = parsePsshAtom(atom);
    if (parsedAtom == null) {
      return null;
    }
    return parsedAtom.first;
  }

  /**
   * Parses the scheme specific data from a PSSH atom. Version 0 and 1 PSSH atoms are supported.
   * <p>
   * The scheme specific data is only parsed if the data is a valid PSSH atom matching the given
   * UUID, or if the data is a valid PSSH atom of any type in the case that the passed UUID is null.
   *
   * @param atom The atom to parse.
   * @param uuid The required UUID of the PSSH atom, or null to accept any UUID.
   * @return The parsed scheme specific data. Null if the input is not a valid PSSH atom, or if the
   *     PSSH atom has an unsupported version, or if the PSSH atom does not match the passed UUID.
   */
  public static byte[] parseSchemeSpecificData(byte[] atom, UUID uuid) {
    Pair<UUID, byte[]> parsedAtom = parsePsshAtom(atom);
    if (parsedAtom == null) {
      return null;
    }
    if (uuid != null && !uuid.equals(parsedAtom.first)) {
      Log.w(TAG, "UUID mismatch. Expected: " + uuid + ", got: " + parsedAtom.first + ".");
      return null;
    }
    return parsedAtom.second;
  }

  /**
   * Parses the UUID and scheme specific data from a PSSH atom. Version 0 and 1 PSSH atoms are
   * supported.
   *
   * @param atom The atom to parse.
   * @return A pair consisting of the parsed UUID and scheme specific data. Null if the input is
   *     not a valid PSSH atom, or if the PSSH atom has an unsupported version.
   */
  private static Pair<UUID, byte[]> parsePsshAtom(byte[] atom) {
    ParsableByteArray atomData = new ParsableByteArray(atom);
    if (atomData.limit() < Atom.FULL_HEADER_SIZE + 16 /* UUID */ + 4 /* DataSize */) {
      // Data too short.
      return null;
    }
    atomData.setPosition(0);
    int atomSize = atomData.readInt();
    if (atomSize != atomData.bytesLeft() + 4) {
      // Not an atom, or incorrect atom size.
      return null;
    }
    int atomType = atomData.readInt();
    if (atomType != Atom.TYPE_pssh) {
      // Not an atom, or incorrect atom type.
      return null;
    }
    int atomVersion = Atom.parseFullAtomVersion(atomData.readInt());
    if (atomVersion > 1) {
      Log.w(TAG, "Unsupported pssh version: " + atomVersion);
      return null;
    }
    UUID uuid = new UUID(atomData.readLong(), atomData.readLong());
    if (atomVersion == 1) {
      int keyIdCount = atomData.readUnsignedIntToInt();
      atomData.skipBytes(16 * keyIdCount);
    }
    int dataSize = atomData.readUnsignedIntToInt();
    if (dataSize != atomData.bytesLeft()) {
      // Incorrect dataSize.
      return null;
    }
    byte[] data = new byte[dataSize];
    atomData.readBytes(data, 0, dataSize);
    return Pair.create(uuid, data);
  }

}
