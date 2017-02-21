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
package kr.wdream.storyshop.exoplayer.dash.mpd;

import kr.wdream.storyshop.exoplayer.C;
import kr.wdream.storyshop.exoplayer.dash.DashSegmentIndex;
import kr.wdream.storyshop.exoplayer.util.Util;
import java.util.List;

/**
 * An approximate representation of a SegmentBase manifest element.
 */
public abstract class SegmentBase {

  /* package */ final RangedUri initialization;
  /* package */ final long timescale;
  /* package */ final long presentationTimeOffset;

  /**
   * @param initialization A {@link RangedUri} corresponding to initialization data, if such data
   *     exists.
   * @param timescale The timescale in units per second.
   * @param presentationTimeOffset The presentation time offset. The value in seconds is the
   *     division of this value and {@code timescale}.
   */
  public SegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset) {
    this.initialization = initialization;
    this.timescale = timescale;
    this.presentationTimeOffset = presentationTimeOffset;
  }

  /**
   * Gets the {@link RangedUri} defining the location of initialization data for a given
   * representation. May be null if no initialization data exists.
   *
   * @param representation The {@link Representation} for which initialization data is required.
   * @return A {@link RangedUri} defining the location of the initialization data, or null.
   */
  public RangedUri getInitialization(Representation representation) {
    return initialization;
  }

  /**
   * Gets the presentation time offset, in microseconds.
   *
   * @return The presentation time offset, in microseconds.
   */
  public long getPresentationTimeOffsetUs() {
    return Util.scaleLargeTimestamp(presentationTimeOffset, C.MICROS_PER_SECOND, timescale);
  }

  /**
   * A {@link SegmentBase} that defines a single segment.
   */
  public static class SingleSegmentBase extends SegmentBase {

    /**
     * The uri of the segment.
     */
    public final String uri;

    /* package */ final long indexStart;
    /* package */ final long indexLength;

    /**
     * @param initialization A {@link RangedUri} corresponding to initialization data, if such data
     *     exists.
     * @param timescale The timescale in units per second.
     * @param presentationTimeOffset The presentation time offset. The value in seconds is the
     *     division of this value and {@code timescale}.
     * @param uri The uri of the segment.
     * @param indexStart The byte offset of the index data in the segment.
     * @param indexLength The length of the index data in bytes.
     */
    public SingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset,
        String uri, long indexStart, long indexLength) {
      super(initialization, timescale, presentationTimeOffset);
      this.uri = uri;
      this.indexStart = indexStart;
      this.indexLength = indexLength;
    }

    /**
     * @param uri The uri of the segment.
     */
    public SingleSegmentBase(String uri) {
      this(null, 1, 0, uri, 0, -1);
    }

    public RangedUri getIndex() {
      return indexLength <= 0 ? null : new RangedUri(uri, null, indexStart, indexLength);
    }

  }

  /**
   * A {@link SegmentBase} that consists of multiple segments.
   */
  public abstract static class MultiSegmentBase extends SegmentBase {

    /* package */ final int startNumber;
    /* package */ final long duration;
    /* package */ final List<SegmentTimelineElement> segmentTimeline;

    /**
     * @param initialization A {@link RangedUri} corresponding to initialization data, if such data
     *     exists.
     * @param timescale The timescale in units per second.
     * @param presentationTimeOffset The presentation time offset. The value in seconds is the
     *     division of this value and {@code timescale}.
     * @param startNumber The sequence number of the first segment.
     * @param duration The duration of each segment in the case of fixed duration segments. The
     *     value in seconds is the division of this value and {@code timescale}. If
     *     {@code segmentTimeline} is non-null then this parameter is ignored.
     * @param segmentTimeline A segment timeline corresponding to the segments. If null, then
     *     segments are assumed to be of fixed duration as specified by the {@code duration}
     *     parameter.
     */
    public MultiSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset,
        int startNumber, long duration, List<SegmentTimelineElement> segmentTimeline) {
      super(initialization, timescale, presentationTimeOffset);
      this.startNumber = startNumber;
      this.duration = duration;
      this.segmentTimeline = segmentTimeline;
    }

    /**
     * @see DashSegmentIndex#getSegmentNum(long, long)
     */
    public int getSegmentNum(long timeUs, long periodDurationUs) {
      final int firstSegmentNum = getFirstSegmentNum();
      int lowIndex = firstSegmentNum;
      int highIndex = getLastSegmentNum(periodDurationUs);
      if (segmentTimeline == null) {
        // All segments are of equal duration (with the possible exception of the last one).
        long durationUs = (duration * C.MICROS_PER_SECOND) / timescale;
        int segmentNum = startNumber + (int) (timeUs / durationUs);
        // Ensure we stay within bounds.
        return segmentNum < lowIndex ? lowIndex
            : highIndex != DashSegmentIndex.INDEX_UNBOUNDED && segmentNum > highIndex ? highIndex
            : segmentNum;
      } else {
        // The high index cannot be unbounded. Identify the segment using binary search.
        while (lowIndex <= highIndex) {
          int midIndex = (lowIndex + highIndex) / 2;
          long midTimeUs = getSegmentTimeUs(midIndex);
          if (midTimeUs < timeUs) {
            lowIndex = midIndex + 1;
          } else if (midTimeUs > timeUs) {
            highIndex = midIndex - 1;
          } else {
            return midIndex;
          }
        }
        return lowIndex == firstSegmentNum ? lowIndex : highIndex;
      }
    }

    /**
     * @see DashSegmentIndex#getDurationUs(int, long)
     */
    public final long getSegmentDurationUs(int sequenceNumber, long periodDurationUs) {
      if (segmentTimeline != null) {
        long duration = segmentTimeline.get(sequenceNumber - startNumber).duration;
        return (duration * C.MICROS_PER_SECOND) / timescale;
      } else {
        return sequenceNumber == getLastSegmentNum(periodDurationUs)
            ? (periodDurationUs - getSegmentTimeUs(sequenceNumber))
            : ((duration * C.MICROS_PER_SECOND) / timescale);
      }
    }

    /**
     * @see DashSegmentIndex#getTimeUs(int)
     */
    public final long getSegmentTimeUs(int sequenceNumber) {
      long unscaledSegmentTime;
      if (segmentTimeline != null) {
        unscaledSegmentTime = segmentTimeline.get(sequenceNumber - startNumber).startTime
            - presentationTimeOffset;
      } else {
        unscaledSegmentTime = (sequenceNumber - startNumber) * duration;
      }
      return Util.scaleLargeTimestamp(unscaledSegmentTime, C.MICROS_PER_SECOND, timescale);
    }

    /**
     * Returns a {@link RangedUri} defining the location of a segment for the given index in the
     * given representation.
     *
     * @see DashSegmentIndex#getSegmentUrl(int)
     */
    public abstract RangedUri getSegmentUrl(Representation representation, int index);

    /**
     * @see DashSegmentIndex#getFirstSegmentNum()
     */
    public int getFirstSegmentNum() {
      return startNumber;
    }

    /**
     * @see DashSegmentIndex#getLastSegmentNum(long)
     */
    public abstract int getLastSegmentNum(long periodDurationUs);

    /**
     * @see DashSegmentIndex#isExplicit()
     */
    public boolean isExplicit() {
      return segmentTimeline != null;
    }

  }

  /**
   * A {@link MultiSegmentBase} that uses a SegmentList to define its segments.
   */
  public static class SegmentList extends MultiSegmentBase {

    /* package */ final List<RangedUri> mediaSegments;

    /**
     * @param initialization A {@link RangedUri} corresponding to initialization data, if such data
     *     exists.
     * @param timescale The timescale in units per second.
     * @param presentationTimeOffset The presentation time offset. The value in seconds is the
     *     division of this value and {@code timescale}.
     * @param startNumber The sequence number of the first segment.
     * @param duration The duration of each segment in the case of fixed duration segments. The
     *     value in seconds is the division of this value and {@code timescale}. If
     *     {@code segmentTimeline} is non-null then this parameter is ignored.
     * @param segmentTimeline A segment timeline corresponding to the segments. If null, then
     *     segments are assumed to be of fixed duration as specified by the {@code duration}
     *     parameter.
     * @param mediaSegments A list of {@link RangedUri}s indicating the locations of the segments.
     */
    public SegmentList(RangedUri initialization, long timescale, long presentationTimeOffset,
        int startNumber, long duration, List<SegmentTimelineElement> segmentTimeline,
        List<RangedUri> mediaSegments) {
      super(initialization, timescale, presentationTimeOffset, startNumber, duration,
          segmentTimeline);
      this.mediaSegments = mediaSegments;
    }

    @Override
    public RangedUri getSegmentUrl(Representation representation, int sequenceNumber) {
      return mediaSegments.get(sequenceNumber - startNumber);
    }

    @Override
    public int getLastSegmentNum(long periodDurationUs) {
      return startNumber + mediaSegments.size() - 1;
    }

    @Override
    public boolean isExplicit() {
      return true;
    }

  }

  /**
   * A {@link MultiSegmentBase} that uses a SegmentTemplate to define its segments.
   */
  public static class SegmentTemplate extends MultiSegmentBase {

    /* package */ final UrlTemplate initializationTemplate;
    /* package */ final UrlTemplate mediaTemplate;

    private final String baseUrl;

    /**
     * @param initialization A {@link RangedUri} corresponding to initialization data, if such data
     *     exists. The value of this parameter is ignored if {@code initializationTemplate} is
     *     non-null.
     * @param timescale The timescale in units per second.
     * @param presentationTimeOffset The presentation time offset. The value in seconds is the
     *     division of this value and {@code timescale}.
     * @param startNumber The sequence number of the first segment.
     * @param duration The duration of each segment in the case of fixed duration segments. The
     *     value in seconds is the division of this value and {@code timescale}. If
     *     {@code segmentTimeline} is non-null then this parameter is ignored.
     * @param segmentTimeline A segment timeline corresponding to the segments. If null, then
     *     segments are assumed to be of fixed duration as specified by the {@code duration}
     *     parameter.
     * @param initializationTemplate A template defining the location of initialization data, if
     *     such data exists. If non-null then the {@code initialization} parameter is ignored. If
     *     null then {@code initialization} will be used.
     * @param mediaTemplate A template defining the location of each media segment.
     * @param baseUrl A url to use as the base for relative urls generated by the templates.
     */
    public SegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset,
        int startNumber, long duration, List<SegmentTimelineElement> segmentTimeline,
        UrlTemplate initializationTemplate, UrlTemplate mediaTemplate, String baseUrl) {
      super(initialization, timescale, presentationTimeOffset, startNumber,
          duration, segmentTimeline);
      this.initializationTemplate = initializationTemplate;
      this.mediaTemplate = mediaTemplate;
      this.baseUrl = baseUrl;
    }

    @Override
    public RangedUri getInitialization(Representation representation) {
      if (initializationTemplate != null) {
        String urlString = initializationTemplate.buildUri(representation.format.id, 0,
            representation.format.bitrate, 0);
        return new RangedUri(baseUrl, urlString, 0, -1);
      } else {
        return super.getInitialization(representation);
      }
    }

    @Override
    public RangedUri getSegmentUrl(Representation representation, int sequenceNumber) {
      long time = 0;
      if (segmentTimeline != null) {
        time = segmentTimeline.get(sequenceNumber - startNumber).startTime;
      } else {
        time = (sequenceNumber - startNumber) * duration;
      }
      String uriString = mediaTemplate.buildUri(representation.format.id, sequenceNumber,
          representation.format.bitrate, time);
      return new RangedUri(baseUrl, uriString, 0, -1);
    }

    @Override
    public int getLastSegmentNum(long periodDurationUs) {
      if (segmentTimeline != null) {
        return segmentTimeline.size() + startNumber - 1;
      } else if (periodDurationUs == C.UNKNOWN_TIME_US) {
        return DashSegmentIndex.INDEX_UNBOUNDED;
      } else {
        long durationUs = (duration * C.MICROS_PER_SECOND) / timescale;
        return startNumber + (int) Util.ceilDivide(periodDurationUs, durationUs) - 1;
      }
    }

  }

  /**
   * Represents a timeline segment from the MPD's SegmentTimeline list.
   */
  public static class SegmentTimelineElement {

    /* package */ long startTime;
    /* package */ long duration;

    /**
     * @param startTime The start time of the element. The value in seconds is the division of this
     *     value and the {@code timescale} of the enclosing element.
     * @param duration The duration of the element. The value in seconds is the division of this
     *     value and the {@code timescale} of the enclosing element.
     */
    public SegmentTimelineElement(long startTime, long duration) {
      this.startTime = startTime;
      this.duration = duration;
    }

  }

}
