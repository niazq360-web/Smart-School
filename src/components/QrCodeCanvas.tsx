import React, { useEffect, useRef } from 'react';

interface QrCodeCanvasProps {
  content: string;
  size?: number;
  primaryColor?: string;
  className?: string;
}

export const QrCodeCanvas: React.FC<QrCodeCanvasProps> = ({
  content,
  size = 120,
  primaryColor = '#1E3A8A',
  className = '',
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.clearRect(0, 0, size, size);

    // Background
    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect(0, 0, size, size);

    const grid = 21; // 21x21 QR Code matrix
    const cell = size / grid;

    function isFinder(r: number, c: number) {
      if (r < 7 && c < 7) return true; // Top-left
      if (r < 7 && c >= grid - 7) return true; // Top-right
      if (r >= grid - 7 && c < 7) return true; // Bottom-left
      return false;
    }

    function drawFinder(sr: number, sc: number) {
      for (let r = 0; r < 7; r++) {
        for (let c = 0; c < 7; c++) {
          const isBorder = r === 0 || r === 6 || c === 0 || c === 6;
          const isCenter = r >= 2 && r <= 4 && c >= 2 && c <= 4;
          if (isBorder || isCenter) {
            ctx.fillStyle = primaryColor;
            ctx.fillRect((sc + c) * cell, (sr + r) * cell, cell * 0.95, cell * 0.95);
          }
        }
      }
    }

    drawFinder(0, 0);
    drawFinder(0, grid - 7);
    drawFinder(grid - 7, 0);

    // Timing pattern
    for (let i = 7; i < grid - 7; i++) {
      if (i % 2 === 0) {
        ctx.fillStyle = primaryColor;
        ctx.fillRect(6 * cell, i * cell, cell * 0.9, cell * 0.9);
        ctx.fillRect(i * cell, 6 * cell, cell * 0.9, cell * 0.9);
      }
    }

    // Deterministic pseudo-random payload pattern
    let hash = 0;
    for (let i = 0; i < content.length; i++) {
      hash = (hash << 5) - hash + content.charCodeAt(i);
      hash |= 0;
    }

    for (let r = 0; r < grid; r++) {
      for (let c = 0; c < grid; c++) {
        if (!isFinder(r, c) && !(r === 6 || c === 6)) {
          const bit = Math.abs((hash ^ (r * 31 + c * 17))) % 3 === 0;
          if (bit) {
            ctx.fillStyle = primaryColor;
            ctx.fillRect(c * cell, r * cell, cell * 0.9, cell * 0.9);
          }
        }
      }
    }
  }, [content, size, primaryColor]);

  return (
    <div className={`p-2 bg-white rounded-xl shadow-sm border border-slate-200 inline-block ${className}`}>
      <canvas ref={canvasRef} width={size} height={size} />
    </div>
  );
};
