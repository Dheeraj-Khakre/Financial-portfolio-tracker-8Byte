// src/app/ai/models/ai.models.ts
export interface AssetAllocation {
  category: string;
  percentage: number; // 0-100
  value: number;
}

export interface AIInsightResponse {
  diversificationScore?: number; // 0..100
  riskLevel?: string; // e.g. "LOW", "MEDIUM", "HIGH"
  recommendations?: string[];
  assetAllocation?: AssetAllocation[];
  summary?: string;
}

export interface AiDialogData {
  portfolioId: number;
  portfolioName?: string;
}
