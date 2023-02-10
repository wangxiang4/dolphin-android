package com.dolphin.core.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 *<p>
 * 默认回收视图边框装饰器
 * 支持绘制 网格布局，交叉网格瀑布流布局，线性布局 边框
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/10
 */
public class DefaultItemDecoration extends RecyclerView.ItemDecoration {

    private final int mWidth;
    private final int mHeight;
    private final BorderDrawable mBorderDrawable;

    public DefaultItemDecoration(@ColorInt int color) {
        this(color, 1, 1);
    }

    /**
     * @param color 边框颜色
     * @param width 边框水平宽度
     * @param height 边框垂直宽度
     */
    public DefaultItemDecoration(@ColorInt int color, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        this.mBorderDrawable = new BorderDrawable(new ColorDrawable(color), mWidth, mHeight);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
        @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = getOrientation(layoutManager);
            int position = parent.getChildLayoutPosition(view);
            int spanCount = getSpanCount(layoutManager);
            int childCount = layoutManager.getItemCount();

            if (orientation == RecyclerView.VERTICAL) {
               offsetVertical(outRect, position, spanCount, childCount);
            } else {
               offsetHorizontal(outRect, position, spanCount, childCount);
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            outRect.set(mWidth, mHeight, mWidth, mHeight);
        }
    }

    /** 设置水平边框间距偏移量 */
    private void offsetHorizontal(Rect outRect, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstColumn && lastColumn) {
                outRect.set(0, 0, 0, 0);
            } else if (firstColumn) {
                /** x|x x */
                outRect.set(0, 0, mWidth, 0);
            } else if (lastColumn) {
                /** x x|x */
                outRect.set(mWidth, 0, 0, 0);
            } else {
                /** x|x|x */
                outRect.set(mWidth, 0, mWidth, 0);
            }
        } else {
            if (firstColumn && firstRaw) {
                /**
                 * x| x x
                 * -
                 * x x x
                 *
                 * x x x
                 *
                 */
                outRect.set(0, 0, mWidth, mHeight);
            } else if (firstColumn && lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 * -
                 * x| x x
                 *
                 */
                outRect.set(0, mHeight, mWidth, 0);
            } else if (lastColumn && firstRaw) {
                /**
                 * x x |x
                 *      -
                 * x x x
                 *
                 * x x x
                 *
                 */
                outRect.set(mWidth, 0, 0, mHeight);
            } else if (lastColumn && lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 *     -
                 * x x|x
                 *
                 */
                outRect.set(mWidth, mHeight, 0, 0);
            } else if (firstColumn) {
                /**
                 * x x x
                 * -
                 * x| x x
                 * -
                 * x x x
                 *
                 */
                outRect.set(0, mHeight, mWidth, mHeight);
            } else if (lastColumn) {
                /**
                 * x x x
                 *     -
                 * x x|x
                 *     -
                 * x x x
                 *
                 */
                outRect.set(mWidth, mHeight, 0, mHeight);
            } else if (firstRaw) {
                /**
                 * x|x|x
                 *   -
                 * x x x
                 *
                 * x x x
                 *
                 */
                outRect.set(mWidth, 0, mWidth, mHeight);
            } else if (lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 *   -
                 * x|x|x
                 *
                 */
                outRect.set(mWidth, mHeight, mWidth, 0);
            } else {
                /**
                 * x x x
                 *   -
                 * x|x|x
                 *   -
                 * x x x
                 *
                 */
                outRect.set(mWidth, mHeight, mWidth, mHeight);
            }
        }
    }

    /** 设置垂直边框间距偏移量 */
    private void offsetVertical(Rect outRect, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.VERTICAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastRaw) {
                outRect.set(0, 0, 0, 0);
            } else if (firstRaw) {
                /**
                 * x
                 * -
                 * x
                 *
                 * x
                 *
                 */
                outRect.set(0, 0, 0, mHeight);
            } else if (lastRaw) {
                /**
                 * x
                 *
                 * x
                 * _
                 * x
                 *
                 */
                outRect.set(0, mHeight, 0, 0);
            } else {
                /**
                 * x
                 * -
                 * x
                 * _
                 * x
                 *
                 */
                outRect.set(0, mHeight, 0, mHeight);
            }
        } else {
            if (firstRaw && firstColumn) {
                /**
                 * x| x x
                 * -
                 * x x x
                 *
                 * x x x
                 *
                 */
                outRect.set(0, 0, mWidth, mHeight);
            } else if (firstRaw && lastColumn) {
                /**
                 * x x |x
                 *      -
                 * x x x
                 *
                 * x x x
                 *
                 */
                outRect.set(mWidth, 0, 0, mHeight);
            } else if (lastRaw && firstColumn) {
                /**
                 * x x x
                 *
                 * x x x
                 * -
                 * x| x x
                 *
                 */
                outRect.set(0, mHeight, mWidth, 0);
            } else if (lastRaw && lastColumn) {
                /**
                 * x x x
                 *
                 * x x x
                 *     -
                 * x x|x
                 *
                 */
                outRect.set(mWidth, mHeight, 0, 0);
            } else if (firstRaw) {
                /**
                 * x|x|x
                 *   -
                 * x x x
                 *
                 * x x x
                 *
                 */
                outRect.set(mWidth, 0, mWidth, mHeight);
            } else if (lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 *   -
                 * x|x|x
                 *
                 */
                outRect.set(mWidth, mHeight, mWidth, 0);
            } else if (firstColumn) {
                /**
                 * x x x
                 * -
                 * x| x x
                 * -
                 * x x x
                 *
                 */
                outRect.set(0, mHeight, mWidth, mHeight);
            } else if (lastColumn) {
                /**
                 * x x x
                 *     -
                 * x x|x
                 *     -
                 * x x x
                 *
                 */
                outRect.set(mWidth, mHeight, 0, mHeight);
            } else {
                /**
                 * x x x
                 *   -
                 * x|x|x
                 *   -
                 * x x x
                 *
                 */
                outRect.set(mWidth, mHeight, mWidth, mHeight);
            }
        }
    }

    /** 获取布局方向 */
    private int getOrientation(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager)layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager)layoutManager).getOrientation();
        }
        return RecyclerView.VERTICAL;
    }

    /** 获取布局行内元素总数 */
    private int getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager)layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        }
        return 1;
    }

    /** 是否是第一行 */
    private boolean isFirstRaw(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            return position < columnCount;
        } else {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        }
    }

    /** 是否是最后一行 */
    private boolean isLastRaw(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % columnCount;
                int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % columnCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / columnCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                    return rawCount == rawPosition;
                }
            }
        } else {
            if (columnCount == 1) return true;
            return (position + 1) % columnCount == 0;
        }
    }

    /** 是否是第一列 */
    private boolean isFirstColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return position % columnCount == 0;
        } else {
            return position < columnCount;
        }
    }

    /** 是否是最后一列 */
    private boolean isLastColumn(int orientation, int position, int columnCount, int childCount) {
        if (orientation == RecyclerView.VERTICAL) {
            if (columnCount == 1) return true;
            return (position + 1) % columnCount == 0;
        } else {
            if (columnCount == 1) {
                return position + 1 == childCount;
            } else {
                int lastRawItemCount = childCount % columnCount;
                int rawCount = (childCount - lastRawItemCount) / columnCount + (lastRawItemCount > 0 ? 1 : 0);

                int rawPositionJudge = (position + 1) % columnCount;
                if (rawPositionJudge == 0) {
                    int positionRaw = (position + 1) / columnCount;
                    return rawCount == positionRaw;
                } else {
                    int rawPosition = (position + 1 - rawPositionJudge) / columnCount + 1;
                    return rawCount == rawPosition;
                }
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        assert layoutManager != null;
        int orientation = getOrientation(layoutManager);
        int spanCount = getSpanCount(layoutManager);
        int childCount = layoutManager.getChildCount();

        if (layoutManager instanceof LinearLayoutManager) {
            canvas.save();
            for (int i = 0; i < childCount; i++) {
                View view = layoutManager.getChildAt(i);
                assert view != null;
                int position = parent.getChildLayoutPosition(view);

                if (orientation == RecyclerView.VERTICAL) {
                    drawVertical(canvas, view, position, spanCount, childCount);
                } else {
                    drawHorizontal(canvas, view, position, spanCount, childCount);
                }
            }
            canvas.restore();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            canvas.save();
            for (int i = 0; i < childCount; i++) {
                View view = layoutManager.getChildAt(i);
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            }
            canvas.restore();
        }
    }

    /** 绘制水平布局边框，支持网格与线性布局 */
    private void drawHorizontal(Canvas canvas, View view, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.HORIZONTAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastColumn) {
            } else if (firstColumn) {
                /** x|x x */
                mBorderDrawable.drawRight(view, canvas);
            } else if (lastColumn) {
                /** x x|x */
                mBorderDrawable.drawLeft(view, canvas);
            } else {
                /** x|x|x */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
            }
        } else {
            if (firstColumn && firstRaw) {
                /**
                 * x| x x
                 * -
                 * x x x
                 *
                 * x x x
                 *
                 */
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (firstColumn && lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 * -
                 * x| x x
                 *
                 */
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
            } else if (lastColumn && firstRaw) {
                /**
                 * x x |x
                 *      -
                 * x x x
                 *
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (lastColumn && lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 *     -
                 * x x|x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
            } else if (firstColumn) {
                /**
                 * x x x
                 * -
                 * x| x x
                 * -
                 * x x x
                 *
                 */
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (lastColumn) {
                /**
                 * x x x
                 *     -
                 * x x|x
                 *     -
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (firstRaw) {
                /**
                 * x|x|x
                 *   -
                 * x x x
                 *
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 *   -
                 * x|x|x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
            } else {
                /**
                 * x x x
                 *   -
                 * x|x|x
                 *   -
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            }
        }
    }

    /** 绘制垂直布局边框，支持网格与线性布局 */
    private void drawVertical(Canvas canvas, View view, int position, int spanCount, int childCount) {
        boolean firstRaw = isFirstRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastRaw = isLastRaw(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean firstColumn = isFirstColumn(RecyclerView.VERTICAL, position, spanCount, childCount);
        boolean lastColumn = isLastColumn(RecyclerView.VERTICAL, position, spanCount, childCount);

        if (spanCount == 1) {
            if (firstRaw && lastRaw) {
            } else if (firstRaw) {
                /**
                 * x
                 * -
                 * x
                 *
                 * x
                 *
                 */
                mBorderDrawable.drawBottom(view, canvas);
            } else if (lastRaw) {
                /**
                 * x
                 *
                 * x
                 * _
                 * x
                 *
                 */
                mBorderDrawable.drawTop(view, canvas);
            } else {
                /**
                 * x
                 * -
                 * x
                 * _
                 * x
                 *
                 */
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            }
        } else {
            if (firstRaw && firstColumn) {
                /**
                 * x| x x
                 * -
                 * x x x
                 *
                 * x x x
                 *
                 */
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (firstRaw && lastColumn) {
                /**
                 * x x |x
                 *      -
                 * x x x
                 *
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (lastRaw && firstColumn) {
                /**
                 * x x x
                 *
                 * x x x
                 * -
                 * x| x x
                 *
                 */
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
            } else if (lastRaw && lastColumn) {
                /**
                 * x x x
                 *
                 * x x x
                 *     -
                 * x x|x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
            } else if (firstRaw) {
                /**
                 * x|x|x
                 *   -
                 * x x x
                 *
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (lastRaw) {
                /**
                 * x x x
                 *
                 * x x x
                 *   -
                 * x|x|x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
            } else if (firstColumn) {
                /**
                 * x x x
                 * -
                 * x| x x
                 * -
                 * x x x
                 *
                 */
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else if (lastColumn) {
                /**
                 * x x x
                 *     -
                 * x x|x
                 *     -
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            } else {
                /**
                 * x x x
                 *   -
                 * x|x|x
                 *   -
                 * x x x
                 *
                 */
                mBorderDrawable.drawLeft(view, canvas);
                mBorderDrawable.drawTop(view, canvas);
                mBorderDrawable.drawRight(view, canvas);
                mBorderDrawable.drawBottom(view, canvas);
            }
        }
    }
}
