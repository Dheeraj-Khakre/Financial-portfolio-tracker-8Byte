
export interface AssetResponse {
  id: number;
  tickerSymbol: string;
  companyName: string;
  quantity: number;
  purchasePrice: number;
  currentPrice: number;
  totalValue: number;
  assetType: 'STOCK' | 'BOND' | 'ETF' | string;
  createdAt: string;  // ISO date
  updatedAt: string;
}

export interface PortfolioResponse {
  id: number;
  name: string;
  description: string;
  totalValue: number;
  assets: AssetResponse[];
  createdAt: string;
  updatedAt: string;
}


export interface PortfolioRequest {
  name: string;
  description?: string;
}

export interface AssetRequest {
  tickerSymbol: string;
  quantity: number;
  purchasePrice: number;
  assetType?: string; // default handled on server
}
