import http from './http';

// ========== 租客管理 ========== //

export interface TenantDetail {
  id: number;
  fullName: string;
  mobileNumber: string;
  wechatId?: string;
  remarkText?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface TenantPageResult {
  pageNo: number;
  pageSize: number;
  total: number;
  records: TenantDetail[];
}

export interface TenantPageParams {
  keyword?: string;
  pageNo?: number;
  pageSize?: number;
}

export interface TenantPayload {
  fullName: string;
  mobileNumber: string;
  wechatId?: string;
  remarkText?: string;
}

export function pageTenants(params: TenantPageParams) {
  return http.get<TenantPageResult>('/tenants/page', { params });
}

export function createTenant(payload: TenantPayload) {
  return http.post<number>('/tenants/create', payload);
}

export function updateTenant(id: number, payload: TenantPayload) {
  return http.post<void>(`/tenants/${id}/update`, payload);
}

export function getTenantDetail(id: number) {
  return http.get<TenantDetail>(`/tenants/${id}`);
}

// ========== 水电单价配置 ========== //

export type ChargeKind = 'ELECTRIC' | 'WATER';

export interface PriceConfigDTO {
  id: number;
  chargeKind: ChargeKind;
  unitPrice: number;
  effectiveDate: string;
  expiredDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface PriceConfigPayload {
  chargeKind: ChargeKind;
  unitPrice: number;
  effectiveDate: string;
  expiredDate?: string | null;
}

export function listPriceConfigs(chargeKind: ChargeKind) {
  return http.get<PriceConfigDTO[]>('/price-configs', { params: { chargeKind } });
}

export function getCurrentPriceConfig(chargeKind: ChargeKind) {
  return http.get<PriceConfigDTO>('/price-configs/current', { params: { chargeKind } });
}

export function createPriceConfig(payload: PriceConfigPayload) {
  return http.post<number>('/price-configs/create', payload);
}

export function updatePriceConfig(id: number, payload: PriceConfigPayload) {
  return http.post<void>(`/price-configs/${id}/update`, payload);
}

// ========== 水电表管理 ========== //

export interface MeterDetail {
  id: number;
  meterCode: string;
  meterKind: ChargeKind;
  installLocation?: string;
  mainMeter?: boolean;
  currentMonthDiff?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface MeterPageResult {
  pageNo: number;
  pageSize: number;
  total: number;
  records: MeterDetail[];
}

export interface MeterPageParams {
  meterKind?: ChargeKind;
  keyword?: string;
  pageNo?: number;
  pageSize?: number;
}

export interface MeterPayload {
  meterCode: string;
  meterKind: ChargeKind;
  installLocation?: string;
  mainMeter?: boolean;
}

export function pageMeters(params: MeterPageParams) {
  return http.get<MeterPageResult>('/meters/page', { params });
}

export function createMeter(payload: MeterPayload) {
  return http.post<number>('/meters/create', payload);
}

export function updateMeter(id: number, payload: MeterPayload) {
  return http.post<void>(`/meters/${id}/update`, payload);
}

export function deleteMeter(id: number) {
  return http.post<void>(`/meters/${id}/delete`);
}

// ========== 水电表月度用量 ========== //

export interface MeterUsageDTO {
  id: number;
  meterId: number;
  usageYear: number;
  usageMonth: number;
  previousReading: number;
  currentReading: number;
  usageValue: number;
  unitPrice?: number;
  totalAmount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface MeterUsagePageResult {
  pageNo: number;
  pageSize: number;
  total: number;
  records: MeterUsageDTO[];
}

export interface MeterUsagePageParams {
  meterId: number;
  usageYear?: number;
  pageNo?: number;
  pageSize?: number;
}

export interface MeterUsagePayload {
  meterId: number;
  usageYear: number;
  usageMonth: number;
  previousReading: number;
  currentReading: number;
}

export function pageMeterUsages(params: MeterUsagePageParams) {
  return http.get<MeterUsagePageResult>('/meter-usages/page', { params });
}

export function upsertMeterUsage(payload: MeterUsagePayload) {
  return http.post<number>('/meter-usages/upsert', payload);
}

export function getMeterUsageByMonth(
  meterId: number,
  usageYear: number,
  usageMonth: number,
) {
  return http.get<MeterUsageDTO>('/meter-usages/by-month', {
    params: { meterId, usageYear, usageMonth },
  });
}

export function deleteMeterUsage(id: number) {
  return http.post<void>(`/meter-usages/${id}/delete`);
}

// ========== 总表-子表集合 ========== //

export interface MeterSetDTO {
  id: number;
  setName: string;
  mainMeterId: number;
  childMeterIds: number[];
  createdAt?: string;
  updatedAt?: string;
}

export interface MeterSetPayload {
  setName: string;
  mainMeterId: number;
  childMeterIds: number[];
}

export function getMeterSetByMain(mainMeterId: number) {
  return http.get<MeterSetDTO>('/meter-sets/by-main', {
    params: { mainMeterId },
  });
}

export function createMeterSet(payload: MeterSetPayload) {
  return http.post<number>('/meter-sets/create', payload);
}

export function updateMeterSet(id: number, payload: MeterSetPayload) {
  return http.post<void>(`/meter-sets/${id}/update`, payload);
}

// ========== 公共场所管理 ========== //

export interface SharedPlaceDetail {
  id: number;
  placeName: string;
  meterIds?: number[];
  tenantIds?: number[];
  /** 本月电表总用量 */
  electricUsageThisMonth?: number;
  /** 本月水表总用量 */
  waterUsageThisMonth?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface SharedPlaceShareItem {
  tenantId: number;
  electricUsage?: number;
  waterUsage?: number;
  electricAmount?: number;
  waterAmount?: number;
}

export interface SharedPlaceShareDetail {
  placeId: number;
  placeName: string;
  usageYear: number;
  usageMonth: number;
  totalElectricUsage?: number;
  totalWaterUsage?: number;
  items: SharedPlaceShareItem[];
}

export interface SharedPlaceSharePayload {
  placeId: number;
  usageYear: number;
  usageMonth: number;
  items: {
    tenantId: number;
    electricUsage?: number;
    waterUsage?: number;
  }[];
}

export interface SharedPlacePageResult {
  pageNo: number;
  pageSize: number;
  total: number;
  records: SharedPlaceDetail[];
}

export interface SharedPlacePageParams {
  keyword?: string;
  pageNo?: number;
  pageSize?: number;
}

export interface SharedPlacePayload {
  placeName: string;
  meterIds?: number[];
  tenantIds?: number[];
}

export function pageSharedPlaces(params: SharedPlacePageParams) {
  return http.get<SharedPlacePageResult>('/shared-places/page', { params });
}

export function createSharedPlace(payload: SharedPlacePayload) {
  return http.post<number>('/shared-places/create', payload);
}

export function updateSharedPlace(id: number, payload: SharedPlacePayload) {
  return http.post<void>(`/shared-places/${id}/update`, payload);
}

export function deleteSharedPlace(id: number) {
  return http.post<void>(`/shared-places/${id}/delete`);
}

export function getSharedPlaceShare(
  id: number,
  usageYear: number,
  usageMonth: number,
) {
  return http.get<SharedPlaceShareDetail>(`/shared-places/${id}/share`, {
    params: { usageYear, usageMonth },
  });
}

export function saveSharedPlaceShare(id: number, payload: SharedPlaceSharePayload) {
  return http.post<void>(`/shared-places/${id}/share`, payload);
}

// ========== 房屋管理 ========== //

export interface HouseDetail {
  id: number;
  houseCode: string;
  houseName?: string;
  locationText?: string;
  rentAmount: number;
  depositAmount: number;
  checkInDate?: string;
  currentTenantId?: number;
  /** 绑定的计量表ID列表（电表+水表） */
  meterIds?: number[];
  /** 本月电表总用量 */
  electricUsageThisMonth?: number;
  /** 本月水表总用量 */
  waterUsageThisMonth?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface HousePageResult {
  pageNo: number;
  pageSize: number;
  total: number;
  records: HouseDetail[];
}

export interface HousePageParams {
  keyword?: string;
  pageNo?: number;
  pageSize?: number;
}

export interface HousePayload {
  houseCode: string;
  houseName?: string;
  locationText?: string;
  rentAmount: number;
  depositAmount: number;
  checkInDate?: string | null;
  currentTenantId?: number | null;
  /** 绑定的计量表ID列表（电表+水表） */
  meterIds?: number[];
}

export function pageHouses(params: HousePageParams) {
  return http.get<HousePageResult>('/houses/page', { params });
}

export function createHouse(payload: HousePayload) {
  return http.post<number>('/houses/create', payload);
}

export function updateHouse(id: number, payload: HousePayload) {
  return http.post<void>(`/houses/${id}/update`, payload);
}

export function deleteHouse(id: number) {
  return http.post<void>(`/houses/${id}/delete`);
}

// ========== 账单中心 ========== //

export interface BillDetail {
  id: number;
  tenantId: number;
  tenantName?: string;
  billYear: number;
  billMonth: number;
  /** 关联房屋ID列表 */
  houseIds?: number[];
  /** 关联房屋展示标签 */
  houseLabels?: string[];
  /** 关联公共场所ID列表 */
  sharedPlaceIds?: number[];
  /** 关联公共场所展示标签 */
  sharedPlaceLabels?: string[];
  /** 房屋维度的水电用量与费用明细 */
  houseItems?: BillHouseItemDTO[];
  /** 公共场所维度的水电用量与费用明细 */
  sharedPlaceItems?: BillSharedPlaceItemDTO[];
  rentAmount?: number;
  waterAmount?: number;
  electricAmount?: number;
  adjustAmount?: number;
  totalAmount?: number;
  settleState?: 'PENDING' | 'SETTLED';
  paidAmount?: number;
  paidTime?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BillHouseItemDTO {
  id: number;
  houseId: number;
  houseLabel: string;
  shareRatio?: number;
  electricUsage?: number;
  waterUsage?: number;
  electricAmount?: number;
  waterAmount?: number;
}

export interface BillSharedPlaceItemDTO {
  id: number;
  sharedPlaceId: number;
  sharedPlaceLabel: string;
  shareRatio?: number;
  electricUsage?: number;
  waterUsage?: number;
  electricAmount?: number;
  waterAmount?: number;
}

export interface BillSharedPlaceAdjustItem {
  id: number;
  electricAmount?: number;
  waterAmount?: number;
}

export interface BillSharedPlaceAdjustPayload {
  items: BillSharedPlaceAdjustItem[];
}

export interface BillRegeneratePayload {
  tenantId?: number;
  billYear: number;
  billMonth: number;
}

export interface BillPageResult {
  pageNo: number;
  pageSize: number;
  total: number;
  records: BillDetail[];
}

export interface BillPageParams {
  tenantId?: number;
  billYear?: number;
  billMonth?: number;
  settleState?: 'PENDING' | 'SETTLED';
  pageNo?: number;
  pageSize?: number;
}

export interface BillPayment {
  id: number;
  billId: number;
  paymentAmount: number;
  paymentTime: string;
  paymentNote?: string;
  createdAt?: string;
}

export interface BillPaymentPayload {
  billId: number;
  paymentAmount: number;
  paymentTime?: string;
  paymentNote?: string;
}

export function pageBills(params: BillPageParams) {
  return http.get<BillPageResult>('/bills/page', { params });
}

export function getBillDetail(id: number) {
  return http.get<BillDetail>(`/bills/${id}`);
}

export function listBillPayments(billId: number) {
  return http.get<BillPayment[]>(`/bills/${billId}/payments`);
}

export function receiveBillPayment(payload: BillPaymentPayload) {
  return http.post<number>('/bills/payments/receive', payload);
}

export function deleteBillPayment(id: number) {
  return http.post<void>(`/bills/payments/${id}/delete`);
}

export function adjustBillSharedPlaces(billId: number, payload: BillSharedPlaceAdjustPayload) {
  return http.post<void>(`/bills/${billId}/shared-places/adjust`, payload);
}

export function regenerateBills(payload: BillRegeneratePayload) {
  return http.post<void>('/bills/regenerate', payload);
}

export function exportBillsPdf(params: BillPageParams) {
  return http.get<ArrayBuffer>('/bills/export', {
    params,
    responseType: 'blob' as any,
  });
}

