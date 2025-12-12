// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.util.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

public final class RangeUtils
{
    static public boolean between(int pos, int from, int to)
    {
	return pos >= from && pos < to;
    }

    static public boolean intersects(int start1, int len1, int start2, int len2)
    {
	if (start1 < start2)
	    return between(start2, start1, start1 + len1);
	return between(start1, start2, start2 + len2);
    }

			   static public int[] commonRange(int start1, int len1, int start2, int len2)
							      {
								  if (!intersects(start1, len1, start2, len2))
								      return null;
								  if (start1 < start2)
								      return new int[]{start2, min(start1 + len1, start2 + len2)};
								  								      return new int[]{start1, min(start1 + len1, start2 + len2)};
							      }

    static public int[] commonRangeByBounds(int from1, int  to1, int from2, int to2)
    {
	return commonRange(from1, to1 - from1, from2, to2 - from2);
    }
}
