<?xml version="1.0" encoding="utf-16"?>
<ShowPlanXML xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" Version="1.5" Build="11.0.7001.0" xmlns="http://schemas.microsoft.com/sqlserver/2004/07/showplan">
  <BatchSequence>
    <Batch>
      <Statements>
        <StmtSimple StatementCompId="1" StatementEstRows="5.84744" StatementId="1" StatementOptmLevel="FULL" StatementOptmEarlyAbortReason="TimeOut" StatementSubTreeCost="1.08322" StatementText="SELECT  STKPRHS1.ERI,STKPRHS1.CODE,A.ABBR AS SUPPABBR,M.DDH AS 订单号,M.PROD AS 物料代码,STKPRHS1.SDATE,M.PRODCNAME AS 物料名称,M.SPEC AS 物料规格,M.VPNO AS 批号,M.REMARK AS 备注,M.DW AS 单位,M.QTY AS 收货数量,M.FZDW AS 辅助单位,M.OUQTY AS 辅助数量,M.W AS 库存,A.CLASNAME AS SUPPCLASNAME,STKPRHS1.SUPP,STKPRHS1.STAFF,S.CNAME AS 采购员姓名,STKPRHS1.QTY,STKPRHS1.CURR,STKPRHS1.TOTAL,STKPRHS1.INVNO,STKPRHS1.PROJNO,STKPRHS1.GWN,B.NAME AS 仓库名称,STKPRHS1.EDITOR,STKPRHS1.SCRUTINY FROM STKPRHS1 LEFT JOIN (SELECT SUPPLIER.CODE AS SUPPCODE,SUPPLIER.ABBR,SUPPCLAS.NAME AS CLASNAME FROM SUPPLIER LEFT JOIN SUPPCLAS ON SUPPCLAS.CODE=SUPPLIER.CLAS) A ON A.SUPPCODE=STKPRHS1.SUPP LEFT JOIN STAFFER S ON S.CODE=STKPRHS1.STAFF LEFT JOIN (SELECT CODE,NAME FROM GODOWN) B ON B.CODE=STKPRHS1.GWN LEFT JOIN (SELECT STKPRHS2.MASTERI,STKPRHS2.ORDERI,(SELECT TOP 1 CQTY FROM PRODQTY WHERE GWN=STKPRHS2.GWN AND PROD=STKPRHS2.PROD)W,(SELECT CNAME FROM PRODUNIT WHERE CODE=STKPRHS2.UNIT)DW,(SELECT TOP 1 CODE FROM ORDCORD2 WHERE ERI=STKPRHS2.ORDERI)DDH,(SELECT CNAME FROM PRODUNIT WHERE CODE=STKPRHS2.OTHERUNIT)FZDW,STKPRHS2.OUQTY,STKPRHS2.SRCERI,STKPRHS2.PROD,(SELECT PRODUCT.CNAME FROM PRODUCT WHERE PRODUCT.CODE=STKPRHS2.PROD)AS PRODCNAME,STKPRHS2.SPEC,STKPRHS2.QTY,STKPRHS2.VPNO,STKPRHS2.REMARK,(SELECT PRODUNIT.CNAME FROM PRODUNIT WHERE PRODUNIT.CODE=STKPRHS2.UNIT) AS UNITCNAME,(SELECT SRCERI FROM ORDSPHS2 WHERE ORDSPHS2.ERI=STKPRHS2.SRCERI) AS SPHSSRCERI,AMT,PRICE FROM STKPRHS2)M ON M.MASTERI=STKPRHS1.ERI LEFT JOIN ORDSPHS2 ON ORDSPHS2.ERI=M.SRCERI LEFT JOIN (SELECT ORDBOM1.SRCERI,ORDBOM2.QTY,ORDBOM2.SUBPROD FROM ORDBOM1 LEFT JOIN ORDBOM2 ON ORDBOM2.MASTERI=ORDBOM1.ERI)K ON K.SRCERI=M.ORDERI AND K.SUBPROD=M.PROD WHERE STKPRHS1.TYPE=0 AND  ISNULL(STKPRHS1.ISMOLDSPHS,0)=0 AND STKPRHS1.GWN IN (SELECT CODE FROM GODOWN WHERE (CODE IN (SELECT GWN FROM GWNSTAFF WHERE STAFF='admin')  OR GWNSTAFFON=0))  ORDER BY STKPRHS1.SDATE DESC,STKPRHS1.CODE DESC&#xD;&#xA;" StatementType="SELECT" QueryHash="0xB920EA283C155565" QueryPlanHash="0xE1FCA0972BE903D9" RetrievedFromCache="false">
          <StatementSetOptions ANSI_NULLS="true" ANSI_PADDING="true" ANSI_WARNINGS="true" ARITHABORT="true" CONCAT_NULL_YIELDS_NULL="true" NUMERIC_ROUNDABORT="false" QUOTED_IDENTIFIER="true" />
          <QueryPlan CachedPlanSize="184" CompileTime="43" CompileCPU="43" CompileMemory="4040">
            <MissingIndexes>
              <MissingIndexGroup Impact="18.7827">
                <MissingIndex Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]">
                  <ColumnGroup Usage="EQUALITY">
                    <Column Name="[TYPE]" ColumnId="4" />
                  </ColumnGroup>
                  <ColumnGroup Usage="INCLUDE">
                    <Column Name="[ERI]" ColumnId="1" />
                    <Column Name="[SUPP]" ColumnId="2" />
                    <Column Name="[SDATE]" ColumnId="3" />
                    <Column Name="[CODE]" ColumnId="5" />
                    <Column Name="[PROJNO]" ColumnId="6" />
                    <Column Name="[GWN]" ColumnId="7" />
                    <Column Name="[TOTAL]" ColumnId="12" />
                    <Column Name="[QTY]" ColumnId="13" />
                    <Column Name="[EDITOR]" ColumnId="14" />
                    <Column Name="[CURR]" ColumnId="18" />
                    <Column Name="[INVNO]" ColumnId="27" />
                    <Column Name="[STAFF]" ColumnId="29" />
                    <Column Name="[SCRUTINY]" ColumnId="31" />
                    <Column Name="[ISMOLDSPHS]" ColumnId="54" />
                  </ColumnGroup>
                </MissingIndex>
              </MissingIndexGroup>
              <MissingIndexGroup Impact="28.1141">
                <MissingIndex Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]">
                  <ColumnGroup Usage="EQUALITY">
                    <Column Name="[TYPE]" ColumnId="4" />
                    <Column Name="[GWN]" ColumnId="7" />
                  </ColumnGroup>
                  <ColumnGroup Usage="INCLUDE">
                    <Column Name="[ERI]" ColumnId="1" />
                    <Column Name="[SUPP]" ColumnId="2" />
                    <Column Name="[SDATE]" ColumnId="3" />
                    <Column Name="[CODE]" ColumnId="5" />
                    <Column Name="[PROJNO]" ColumnId="6" />
                    <Column Name="[TOTAL]" ColumnId="12" />
                    <Column Name="[QTY]" ColumnId="13" />
                    <Column Name="[EDITOR]" ColumnId="14" />
                    <Column Name="[CURR]" ColumnId="18" />
                    <Column Name="[INVNO]" ColumnId="27" />
                    <Column Name="[STAFF]" ColumnId="29" />
                    <Column Name="[SCRUTINY]" ColumnId="31" />
                    <Column Name="[ISMOLDSPHS]" ColumnId="54" />
                  </ColumnGroup>
                </MissingIndex>
              </MissingIndexGroup>
            </MissingIndexes>
            <MemoryGrantInfo SerialRequiredMemory="1536" SerialDesiredMemory="1688" />
            <OptimizerHardwareDependentProperties EstimatedAvailableMemoryGrant="78643" EstimatedPagesCached="19660" EstimatedAvailableDegreeOfParallelism="2" />
            <RelOp AvgRowSize="875" EstimateCPU="2.44423E-05" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="5.84744" LogicalOp="Left Outer Join" NodeId="0" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="1.08322">
              <OutputList>
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                <ColumnReference Column="Expr1010" />
                <ColumnReference Column="Expr1011" />
                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CNAME" />
                <ColumnReference Column="Expr1018" />
                <ColumnReference Column="Expr1028" />
                <ColumnReference Column="Expr1033" />
                <ColumnReference Column="Expr1038" />
                <ColumnReference Column="Expr1043" />
                <ColumnReference Column="Expr1044" />
                <ColumnReference Column="Expr1046" />
                <ColumnReference Column="Expr1051" />
                <ColumnReference Column="Expr1052" />
                <ColumnReference Column="Expr1053" />
                <ColumnReference Column="Expr1054" />
                <ColumnReference Column="Expr1055" />
              </OutputList>
              <NestedLoops Optimized="false">
                <OuterReferences>
                  <ColumnReference Column="Expr1023" />
                  <ColumnReference Column="Expr1046" />
                </OuterReferences>
                <RelOp AvgRowSize="885" EstimateCPU="2.44423E-05" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="5.84744" LogicalOp="Left Outer Join" NodeId="1" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.666899">
                  <OutputList>
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                    <ColumnReference Column="Expr1010" />
                    <ColumnReference Column="Expr1011" />
                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CNAME" />
                    <ColumnReference Column="Expr1018" />
                    <ColumnReference Column="Expr1023" />
                    <ColumnReference Column="Expr1028" />
                    <ColumnReference Column="Expr1033" />
                    <ColumnReference Column="Expr1038" />
                    <ColumnReference Column="Expr1043" />
                    <ColumnReference Column="Expr1044" />
                    <ColumnReference Column="Expr1046" />
                    <ColumnReference Column="Expr1051" />
                    <ColumnReference Column="Expr1052" />
                    <ColumnReference Column="Expr1053" />
                    <ColumnReference Column="Expr1054" />
                    <ColumnReference Column="Expr1055" />
                  </OutputList>
                  <NestedLoops Optimized="false">
                    <OuterReferences>
                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                    </OuterReferences>
                    <RelOp AvgRowSize="417" EstimateCPU="0.000451887" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="5.14795" LogicalOp="Left Outer Join" NodeId="2" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.401537">
                      <OutputList>
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                        <ColumnReference Column="Expr1010" />
                        <ColumnReference Column="Expr1011" />
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CNAME" />
                        <ColumnReference Column="Expr1018" />
                      </OutputList>
                      <NestedLoops Optimized="false">
                        <Predicate>
                          <ScalarOperator ScalarString="[PILOT2004].[dbo].[GODOWN].[CODE]=[PILOT2004].[dbo].[STKPRHS1].[GWN]">
                            <Compare CompareOp="EQ">
                              <ScalarOperator>
                                <Identifier>
                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                </Identifier>
                              </ScalarOperator>
                              <ScalarOperator>
                                <Identifier>
                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                </Identifier>
                              </ScalarOperator>
                            </Compare>
                          </ScalarOperator>
                        </Predicate>
                        <RelOp AvgRowSize="314" EstimateCPU="0.00068859" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="5.14795" LogicalOp="Left Outer Join" NodeId="3" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.397315">
                          <OutputList>
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                            <ColumnReference Column="Expr1010" />
                            <ColumnReference Column="Expr1011" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CNAME" />
                          </OutputList>
                          <NestedLoops Optimized="false">
                            <Predicate>
                              <ScalarOperator ScalarString="[PILOT2004].[dbo].[STAFFER].[CODE] as [S].[CODE]=[PILOT2004].[dbo].[STKPRHS1].[STAFF]">
                                <Compare CompareOp="EQ">
                                  <ScalarOperator>
                                    <Identifier>
                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CODE" />
                                    </Identifier>
                                  </ScalarOperator>
                                  <ScalarOperator>
                                    <Identifier>
                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                                    </Identifier>
                                  </ScalarOperator>
                                </Compare>
                              </ScalarOperator>
                            </Predicate>
                            <RelOp AvgRowSize="262" EstimateCPU="2.15184E-05" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="5.14795" LogicalOp="Left Outer Join" NodeId="4" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.392048">
                              <OutputList>
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                                <ColumnReference Column="Expr1010" />
                                <ColumnReference Column="Expr1011" />
                              </OutputList>
                              <NestedLoops Optimized="false">
                                <OuterReferences>
                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                                </OuterReferences>
                                <RelOp AvgRowSize="173" EstimateCPU="0.000119158" EstimateIO="0.0112613" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="5.14795" LogicalOp="Sort" NodeId="5" Parallel="false" PhysicalOp="Sort" EstimatedTotalSubtreeCost="0.369135">
                                  <OutputList>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                                  </OutputList>
                                  <MemoryFractions Input="0.210526" Output="1" />
                                  <Sort Distinct="false">
                                    <OrderBy>
                                      <OrderByColumn Ascending="false">
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                                      </OrderByColumn>
                                      <OrderByColumn Ascending="false">
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                                      </OrderByColumn>
                                    </OrderBy>
                                    <RelOp AvgRowSize="173" EstimateCPU="0.0707059" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="5.14795" LogicalOp="Inner Join" NodeId="7" Parallel="false" PhysicalOp="Hash Match" EstimatedTotalSubtreeCost="0.357755">
                                      <OutputList>
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                                      </OutputList>
                                      <MemoryFractions Input="1" Output="0.789474" />
                                      <Hash>
                                        <DefinedValues />
                                        <HashKeysBuild>
                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                        </HashKeysBuild>
                                        <HashKeysProbe>
                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                        </HashKeysProbe>
                                        <ProbeResidual>
                                          <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS1].[GWN]=[PILOT2004].[dbo].[GODOWN].[CODE]">
                                            <Compare CompareOp="EQ">
                                              <ScalarOperator>
                                                <Identifier>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                                </Identifier>
                                              </ScalarOperator>
                                              <ScalarOperator>
                                                <Identifier>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                                </Identifier>
                                              </ScalarOperator>
                                            </Compare>
                                          </ScalarOperator>
                                        </ProbeResidual>
                                        <RelOp AvgRowSize="17" EstimateCPU="8.778E-05" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="18.9" LogicalOp="Left Semi Join" NodeId="8" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.00342622">
                                          <OutputList>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                          </OutputList>
                                          <NestedLoops Optimized="false">
                                            <OuterReferences>
                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="GWNSTAFFON" />
                                            </OuterReferences>
                                            <RelOp AvgRowSize="19" EstimateCPU="0.0001801" EstimateIO="0.003125" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="21" LogicalOp="Clustered Index Scan" NodeId="9" Parallel="false" PhysicalOp="Clustered Index Scan" EstimatedTotalSubtreeCost="0.0033051" TableCardinality="21">
                                              <OutputList>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="GWNSTAFFON" />
                                              </OutputList>
                                              <IndexScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                                <DefinedValues>
                                                  <DefinedValue>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                                  </DefinedValue>
                                                  <DefinedValue>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="GWNSTAFFON" />
                                                  </DefinedValue>
                                                </DefinedValues>
                                                <Object Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Index="[GODOWN_PK]" TableReferenceId="2" IndexKind="Clustered" />
                                              </IndexScan>
                                            </RelOp>
                                            <RelOp AvgRowSize="9" EstimateCPU="2E-07" EstimateIO="0" EstimateRebinds="20" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Concatenation" NodeId="10" Parallel="false" PhysicalOp="Concatenation" EstimatedTotalSubtreeCost="3.3337E-05">
                                              <OutputList />
                                              <Concat>
                                                <DefinedValues />
                                                <RelOp AvgRowSize="9" EstimateCPU="4.8E-07" EstimateIO="0" EstimateRebinds="20" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Filter" NodeId="11" Parallel="false" PhysicalOp="Filter" EstimatedTotalSubtreeCost="3.1237E-05">
                                                  <OutputList />
                                                  <Filter StartupExpression="true">
                                                    <RelOp AvgRowSize="9" EstimateCPU="1.157E-06" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="20" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Constant Scan" NodeId="12" Parallel="false" PhysicalOp="Constant Scan" EstimatedTotalSubtreeCost="2.1157E-05">
                                                      <OutputList />
                                                      <ConstantScan />
                                                    </RelOp>
                                                    <Predicate>
                                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[GODOWN].[GWNSTAFFON]=(0)">
                                                        <Compare CompareOp="EQ">
                                                          <ScalarOperator>
                                                            <Identifier>
                                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="GWNSTAFFON" />
                                                            </Identifier>
                                                          </ScalarOperator>
                                                          <ScalarOperator>
                                                            <Const ConstValue="(0)" />
                                                          </ScalarOperator>
                                                        </Compare>
                                                      </ScalarOperator>
                                                    </Predicate>
                                                  </Filter>
                                                </RelOp>
                                                <RelOp AvgRowSize="15" EstimateCPU="7.96E-05" EstimateIO="0.0032035" EstimateRebinds="0" EstimateRewinds="20" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Table Scan" NodeId="14" Parallel="false" PhysicalOp="Table Scan" EstimatedTotalSubtreeCost="0.0048751" TableCardinality="0">
                                                  <OutputList />
                                                  <TableScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                                    <DefinedValues />
                                                    <Object Database="[PILOT2004]" Schema="[dbo]" Table="[GWNSTAFF]" IndexKind="Heap" />
                                                    <Predicate>
                                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[GODOWN].[CODE]=[PILOT2004].[dbo].[GWNSTAFF].[GWN] AND [PILOT2004].[dbo].[GWNSTAFF].[STAFF]='admin'">
                                                        <Logical Operation="AND">
                                                          <ScalarOperator>
                                                            <Compare CompareOp="EQ">
                                                              <ScalarOperator>
                                                                <Identifier>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                                                </Identifier>
                                                              </ScalarOperator>
                                                              <ScalarOperator>
                                                                <Identifier>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GWNSTAFF]" Column="GWN" />
                                                                </Identifier>
                                                              </ScalarOperator>
                                                            </Compare>
                                                          </ScalarOperator>
                                                          <ScalarOperator>
                                                            <Compare CompareOp="EQ">
                                                              <ScalarOperator>
                                                                <Identifier>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GWNSTAFF]" Column="STAFF" />
                                                                </Identifier>
                                                              </ScalarOperator>
                                                              <ScalarOperator>
                                                                <Const ConstValue="'admin'" />
                                                              </ScalarOperator>
                                                            </Compare>
                                                          </ScalarOperator>
                                                        </Logical>
                                                      </ScalarOperator>
                                                    </Predicate>
                                                  </TableScan>
                                                </RelOp>
                                              </Concat>
                                            </RelOp>
                                          </NestedLoops>
                                        </RelOp>
                                        <RelOp AvgRowSize="177" EstimateCPU="0.0089537" EstimateIO="0.266829" EstimateRebinds="0" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="7875" LogicalOp="Clustered Index Scan" NodeId="16" Parallel="false" PhysicalOp="Clustered Index Scan" EstimatedTotalSubtreeCost="0.275782" TableCardinality="7997">
                                          <OutputList>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                                          </OutputList>
                                          <IndexScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                            <DefinedValues>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SDATE" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CODE" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="PROJNO" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="GWN" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TOTAL" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="QTY" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="EDITOR" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="CURR" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="INVNO" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="STAFF" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SCRUTINY" />
                                              </DefinedValue>
                                            </DefinedValues>
                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Index="[STKPRHS1_PK]" IndexKind="Clustered" />
                                            <Predicate>
                                              <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS1].[TYPE]=(0) AND isnull([PILOT2004].[dbo].[STKPRHS1].[ISMOLDSPHS],(0))=(0)">
                                                <Logical Operation="AND">
                                                  <ScalarOperator>
                                                    <Compare CompareOp="EQ">
                                                      <ScalarOperator>
                                                        <Identifier>
                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="TYPE" />
                                                        </Identifier>
                                                      </ScalarOperator>
                                                      <ScalarOperator>
                                                        <Const ConstValue="(0)" />
                                                      </ScalarOperator>
                                                    </Compare>
                                                  </ScalarOperator>
                                                  <ScalarOperator>
                                                    <Compare CompareOp="EQ">
                                                      <ScalarOperator>
                                                        <Intrinsic FunctionName="isnull">
                                                          <ScalarOperator>
                                                            <Identifier>
                                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ISMOLDSPHS" />
                                                            </Identifier>
                                                          </ScalarOperator>
                                                          <ScalarOperator>
                                                            <Const ConstValue="(0)" />
                                                          </ScalarOperator>
                                                        </Intrinsic>
                                                      </ScalarOperator>
                                                      <ScalarOperator>
                                                        <Const ConstValue="(0)" />
                                                      </ScalarOperator>
                                                    </Compare>
                                                  </ScalarOperator>
                                                </Logical>
                                              </ScalarOperator>
                                            </Predicate>
                                          </IndexScan>
                                        </RelOp>
                                      </Hash>
                                    </RelOp>
                                  </Sort>
                                </RelOp>
                                <RelOp AvgRowSize="98" EstimateCPU="1E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Compute Scalar" NodeId="20" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.0228914">
                                  <OutputList>
                                    <ColumnReference Column="Expr1010" />
                                    <ColumnReference Column="Expr1011" />
                                  </OutputList>
                                  <ComputeScalar>
                                    <DefinedValues>
                                      <DefinedValue>
                                        <ColumnReference Column="Expr1011" />
                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[SUPPCLAS].[NAME]">
                                          <Identifier>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Column="NAME" />
                                          </Identifier>
                                        </ScalarOperator>
                                      </DefinedValue>
                                    </DefinedValues>
                                    <RelOp AvgRowSize="98" EstimateCPU="3.762E-05" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Left Outer Join" NodeId="21" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.0228909">
                                      <OutputList>
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Column="NAME" />
                                        <ColumnReference Column="Expr1010" />
                                      </OutputList>
                                      <NestedLoops Optimized="false">
                                        <Predicate>
                                          <ScalarOperator ScalarString="[PILOT2004].[dbo].[SUPPCLAS].[CODE]=[PILOT2004].[dbo].[SUPPLIER].[CLAS]">
                                            <Compare CompareOp="EQ">
                                              <ScalarOperator>
                                                <Identifier>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Column="CODE" />
                                                </Identifier>
                                              </ScalarOperator>
                                              <ScalarOperator>
                                                <Identifier>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="CLAS" />
                                                </Identifier>
                                              </ScalarOperator>
                                            </Compare>
                                          </ScalarOperator>
                                        </Predicate>
                                        <RelOp AvgRowSize="92" EstimateCPU="1E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Compute Scalar" NodeId="22" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.0190247">
                                          <OutputList>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="CLAS" />
                                            <ColumnReference Column="Expr1010" />
                                          </OutputList>
                                          <ComputeScalar>
                                            <DefinedValues>
                                              <DefinedValue>
                                                <ColumnReference Column="Expr1010" />
                                                <ScalarOperator ScalarString="[PILOT2004].[dbo].[SUPPLIER].[ABBR]">
                                                  <Identifier>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ABBR" />
                                                  </Identifier>
                                                </ScalarOperator>
                                              </DefinedValue>
                                            </DefinedValues>
                                            <RelOp AvgRowSize="92" EstimateCPU="4.18E-06" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Inner Join" NodeId="23" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.0190242">
                                              <OutputList>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="CLAS" />
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ABBR" />
                                              </OutputList>
                                              <NestedLoops Optimized="false">
                                                <OuterReferences>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ERI" />
                                                </OuterReferences>
                                                <RelOp AvgRowSize="27" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Index Seek" NodeId="24" Parallel="false" PhysicalOp="Index Seek" EstimatedTotalSubtreeCost="0.00671134" TableCardinality="274">
                                                  <OutputList>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ERI" />
                                                  </OutputList>
                                                  <IndexScan Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                                    <DefinedValues>
                                                      <DefinedValue>
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ERI" />
                                                      </DefinedValue>
                                                    </DefinedValues>
                                                    <Object Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Index="[SUPPLIER_0]" IndexKind="NonClustered" />
                                                    <SeekPredicates>
                                                      <SeekPredicateNew>
                                                        <SeekKeys>
                                                          <Prefix ScanType="EQ">
                                                            <RangeColumns>
                                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="CODE" />
                                                            </RangeColumns>
                                                            <RangeExpressions>
                                                              <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS1].[SUPP]">
                                                                <Identifier>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="SUPP" />
                                                                </Identifier>
                                                              </ScalarOperator>
                                                            </RangeExpressions>
                                                          </Prefix>
                                                        </SeekKeys>
                                                      </SeekPredicateNew>
                                                    </SeekPredicates>
                                                  </IndexScan>
                                                </RelOp>
                                                <RelOp AvgRowSize="98" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Clustered Index Seek" NodeId="26" Parallel="false" PhysicalOp="Clustered Index Seek" EstimatedTotalSubtreeCost="0.0122913" TableCardinality="274">
                                                  <OutputList>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="CLAS" />
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ABBR" />
                                                  </OutputList>
                                                  <IndexScan Lookup="true" Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                                    <DefinedValues>
                                                      <DefinedValue>
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="CLAS" />
                                                      </DefinedValue>
                                                      <DefinedValue>
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ABBR" />
                                                      </DefinedValue>
                                                    </DefinedValues>
                                                    <Object Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Index="[SUPPLIER_PK]" TableReferenceId="-1" IndexKind="Clustered" />
                                                    <SeekPredicates>
                                                      <SeekPredicateNew>
                                                        <SeekKeys>
                                                          <Prefix ScanType="EQ">
                                                            <RangeColumns>
                                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ERI" />
                                                            </RangeColumns>
                                                            <RangeExpressions>
                                                              <ScalarOperator ScalarString="[PILOT2004].[dbo].[SUPPLIER].[ERI]">
                                                                <Identifier>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPLIER]" Column="ERI" />
                                                                </Identifier>
                                                              </ScalarOperator>
                                                            </RangeExpressions>
                                                          </Prefix>
                                                        </SeekKeys>
                                                      </SeekPredicateNew>
                                                    </SeekPredicates>
                                                  </IndexScan>
                                                </RelOp>
                                              </NestedLoops>
                                            </RelOp>
                                          </ComputeScalar>
                                        </RelOp>
                                        <RelOp AvgRowSize="27" EstimateCPU="8.84E-05" EstimateIO="0.0032035" EstimateRebinds="0" EstimateRewinds="4.14795" EstimatedExecutionMode="Row" EstimateRows="9" LogicalOp="Clustered Index Scan" NodeId="35" Parallel="false" PhysicalOp="Clustered Index Scan" EstimatedTotalSubtreeCost="0.00365858" TableCardinality="9">
                                          <OutputList>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Column="CODE" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Column="NAME" />
                                          </OutputList>
                                          <IndexScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                            <DefinedValues>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Column="CODE" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Column="NAME" />
                                              </DefinedValue>
                                            </DefinedValues>
                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[SUPPCLAS]" Index="[SUPPCLAS_PK]" IndexKind="Clustered" />
                                          </IndexScan>
                                        </RelOp>
                                      </NestedLoops>
                                    </RelOp>
                                  </ComputeScalar>
                                </RelOp>
                              </NestedLoops>
                            </RelOp>
                            <RelOp AvgRowSize="66" EstimateCPU="0.0001137" EstimateIO="0.00394424" EstimateRebinds="0" EstimateRewinds="4.14795" EstimatedExecutionMode="Row" EstimateRows="32" LogicalOp="Clustered Index Scan" NodeId="40" Parallel="false" PhysicalOp="Clustered Index Scan" EstimatedTotalSubtreeCost="0.00452956" TableCardinality="32">
                              <OutputList>
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CODE" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CNAME" />
                              </OutputList>
                              <IndexScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                <DefinedValues>
                                  <DefinedValue>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CODE" />
                                  </DefinedValue>
                                  <DefinedValue>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Alias="[S]" Column="CNAME" />
                                  </DefinedValue>
                                </DefinedValues>
                                <Object Database="[PILOT2004]" Schema="[dbo]" Table="[STAFFER]" Index="[STAFFER_PK]" Alias="[S]" IndexKind="Clustered" />
                              </IndexScan>
                            </RelOp>
                          </NestedLoops>
                        </RelOp>
                        <RelOp AvgRowSize="119" EstimateCPU="2.1E-06" EstimateIO="0" EstimateRebinds="0" EstimateRewinds="4.14795" EstimatedExecutionMode="Row" EstimateRows="21" LogicalOp="Compute Scalar" NodeId="42" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.00373734">
                          <OutputList>
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                            <ColumnReference Column="Expr1018" />
                          </OutputList>
                          <ComputeScalar>
                            <DefinedValues>
                              <DefinedValue>
                                <ColumnReference Column="Expr1018" />
                                <ScalarOperator ScalarString="[PILOT2004].[dbo].[GODOWN].[NAME]">
                                  <Identifier>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="NAME" />
                                  </Identifier>
                                </ScalarOperator>
                              </DefinedValue>
                            </DefinedValues>
                            <RelOp AvgRowSize="119" EstimateCPU="0.0001016" EstimateIO="0.0032035" EstimateRebinds="0" EstimateRewinds="4.14795" EstimatedExecutionMode="Row" EstimateRows="21" LogicalOp="Clustered Index Scan" NodeId="43" Parallel="false" PhysicalOp="Clustered Index Scan" EstimatedTotalSubtreeCost="0.00372653" TableCardinality="21">
                              <OutputList>
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="NAME" />
                              </OutputList>
                              <IndexScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                <DefinedValues>
                                  <DefinedValue>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="CODE" />
                                  </DefinedValue>
                                  <DefinedValue>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Column="NAME" />
                                  </DefinedValue>
                                </DefinedValues>
                                <Object Database="[PILOT2004]" Schema="[dbo]" Table="[GODOWN]" Index="[GODOWN_PK]" TableReferenceId="1" IndexKind="Clustered" />
                              </IndexScan>
                            </RelOp>
                          </ComputeScalar>
                        </RelOp>
                      </NestedLoops>
                    </RelOp>
                    <RelOp AvgRowSize="477" EstimateCPU="1.13588E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1.13588" LogicalOp="Compute Scalar" NodeId="48" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.265338">
                      <OutputList>
                        <ColumnReference Column="Expr1023" />
                        <ColumnReference Column="Expr1028" />
                        <ColumnReference Column="Expr1033" />
                        <ColumnReference Column="Expr1038" />
                        <ColumnReference Column="Expr1043" />
                        <ColumnReference Column="Expr1044" />
                        <ColumnReference Column="Expr1046" />
                        <ColumnReference Column="Expr1051" />
                        <ColumnReference Column="Expr1052" />
                        <ColumnReference Column="Expr1053" />
                        <ColumnReference Column="Expr1054" />
                        <ColumnReference Column="Expr1055" />
                      </OutputList>
                      <ComputeScalar>
                        <DefinedValues>
                          <DefinedValue>
                            <ColumnReference Column="Expr1051" />
                            <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODUCT].[CNAME]">
                              <Identifier>
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="CNAME" />
                              </Identifier>
                            </ScalarOperator>
                          </DefinedValue>
                        </DefinedValues>
                        <RelOp AvgRowSize="477" EstimateCPU="4.74796E-06" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1.13588" LogicalOp="Inner Join" NodeId="49" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.265337">
                          <OutputList>
                            <ColumnReference Column="Expr1023" />
                            <ColumnReference Column="Expr1028" />
                            <ColumnReference Column="Expr1033" />
                            <ColumnReference Column="Expr1038" />
                            <ColumnReference Column="Expr1043" />
                            <ColumnReference Column="Expr1044" />
                            <ColumnReference Column="Expr1046" />
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="CNAME" />
                            <ColumnReference Column="Expr1052" />
                            <ColumnReference Column="Expr1053" />
                            <ColumnReference Column="Expr1054" />
                            <ColumnReference Column="Expr1055" />
                          </OutputList>
                          <NestedLoops Optimized="false">
                            <PassThru>
                              <ScalarOperator ScalarString="[IsBaseRow1048] IS NULL">
                                <Compare CompareOp="IS">
                                  <ScalarOperator>
                                    <Identifier>
                                      <ColumnReference Column="IsBaseRow1048" />
                                    </Identifier>
                                  </ScalarOperator>
                                  <ScalarOperator>
                                    <Const ConstValue="NULL" />
                                  </ScalarOperator>
                                </Compare>
                              </ScalarOperator>
                            </PassThru>
                            <OuterReferences>
                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="ERI" />
                            </OuterReferences>
                            <RelOp AvgRowSize="419" EstimateCPU="4.74796E-06" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1.13588" LogicalOp="Left Outer Join" NodeId="50" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.249253">
                              <OutputList>
                                <ColumnReference Column="Expr1023" />
                                <ColumnReference Column="Expr1028" />
                                <ColumnReference Column="Expr1033" />
                                <ColumnReference Column="Expr1038" />
                                <ColumnReference Column="Expr1043" />
                                <ColumnReference Column="Expr1044" />
                                <ColumnReference Column="Expr1046" />
                                <ColumnReference Column="IsBaseRow1048" />
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="ERI" />
                                <ColumnReference Column="Expr1052" />
                                <ColumnReference Column="Expr1053" />
                                <ColumnReference Column="Expr1054" />
                                <ColumnReference Column="Expr1055" />
                              </OutputList>
                              <NestedLoops Optimized="false">
                                <OuterReferences>
                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                </OuterReferences>
                                <RelOp AvgRowSize="410" EstimateCPU="3.00252E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1.13588" LogicalOp="Compute Scalar" NodeId="51" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.233837">
                                  <OutputList>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                    <ColumnReference Column="Expr1023" />
                                    <ColumnReference Column="Expr1028" />
                                    <ColumnReference Column="Expr1033" />
                                    <ColumnReference Column="Expr1038" />
                                    <ColumnReference Column="Expr1043" />
                                    <ColumnReference Column="Expr1044" />
                                    <ColumnReference Column="Expr1046" />
                                    <ColumnReference Column="Expr1052" />
                                    <ColumnReference Column="Expr1053" />
                                    <ColumnReference Column="Expr1054" />
                                    <ColumnReference Column="Expr1055" />
                                  </OutputList>
                                  <ComputeScalar>
                                    <DefinedValues>
                                      <DefinedValue>
                                        <ColumnReference Column="Expr1043" />
                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODUNIT].[CNAME]">
                                          <Identifier>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                          </Identifier>
                                        </ScalarOperator>
                                      </DefinedValue>
                                    </DefinedValues>
                                    <RelOp AvgRowSize="410" EstimateCPU="0.000640077" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.00252" LogicalOp="Left Outer Join" NodeId="52" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.233835">
                                      <OutputList>
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                        <ColumnReference Column="Expr1023" />
                                        <ColumnReference Column="Expr1028" />
                                        <ColumnReference Column="Expr1033" />
                                        <ColumnReference Column="Expr1038" />
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                        <ColumnReference Column="Expr1044" />
                                        <ColumnReference Column="Expr1046" />
                                        <ColumnReference Column="Expr1052" />
                                        <ColumnReference Column="Expr1053" />
                                        <ColumnReference Column="Expr1054" />
                                        <ColumnReference Column="Expr1055" />
                                      </OutputList>
                                      <NestedLoops Optimized="false">
                                        <Predicate>
                                          <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODUNIT].[CODE]=[PILOT2004].[dbo].[STKPRHS2].[OTHERUNIT]">
                                            <Compare CompareOp="EQ">
                                              <ScalarOperator>
                                                <Identifier>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CODE" />
                                                </Identifier>
                                              </ScalarOperator>
                                              <ScalarOperator>
                                                <Identifier>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                </Identifier>
                                              </ScalarOperator>
                                            </Compare>
                                          </ScalarOperator>
                                        </Predicate>
                                        <RelOp AvgRowSize="410" EstimateCPU="3.00252E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.00252" LogicalOp="Compute Scalar" NodeId="53" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.22502">
                                          <OutputList>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                            <ColumnReference Column="Expr1023" />
                                            <ColumnReference Column="Expr1028" />
                                            <ColumnReference Column="Expr1033" />
                                            <ColumnReference Column="Expr1038" />
                                            <ColumnReference Column="Expr1044" />
                                            <ColumnReference Column="Expr1046" />
                                            <ColumnReference Column="Expr1052" />
                                            <ColumnReference Column="Expr1053" />
                                            <ColumnReference Column="Expr1054" />
                                            <ColumnReference Column="Expr1055" />
                                          </OutputList>
                                          <ComputeScalar>
                                            <DefinedValues>
                                              <DefinedValue>
                                                <ColumnReference Column="Expr1038" />
                                                <ScalarOperator ScalarString="[PILOT2004].[dbo].[ORDCORD2].[CODE]">
                                                  <Identifier>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDCORD2]" Column="CODE" />
                                                  </Identifier>
                                                </ScalarOperator>
                                              </DefinedValue>
                                            </DefinedValues>
                                            <RelOp AvgRowSize="410" EstimateCPU="1.25505E-05" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.00252" LogicalOp="Left Outer Join" NodeId="54" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.225018">
                                              <OutputList>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                <ColumnReference Column="Expr1023" />
                                                <ColumnReference Column="Expr1028" />
                                                <ColumnReference Column="Expr1033" />
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDCORD2]" Column="CODE" />
                                                <ColumnReference Column="Expr1044" />
                                                <ColumnReference Column="Expr1046" />
                                                <ColumnReference Column="Expr1052" />
                                                <ColumnReference Column="Expr1053" />
                                                <ColumnReference Column="Expr1054" />
                                                <ColumnReference Column="Expr1055" />
                                              </OutputList>
                                              <NestedLoops Optimized="false">
                                                <OuterReferences>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                </OuterReferences>
                                                <RelOp AvgRowSize="408" EstimateCPU="3.28498E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.00252" LogicalOp="Compute Scalar" NodeId="55" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.179355">
                                                  <OutputList>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                    <ColumnReference Column="Expr1023" />
                                                    <ColumnReference Column="Expr1028" />
                                                    <ColumnReference Column="Expr1033" />
                                                    <ColumnReference Column="Expr1044" />
                                                    <ColumnReference Column="Expr1046" />
                                                    <ColumnReference Column="Expr1052" />
                                                    <ColumnReference Column="Expr1053" />
                                                    <ColumnReference Column="Expr1054" />
                                                    <ColumnReference Column="Expr1055" />
                                                  </OutputList>
                                                  <ComputeScalar>
                                                    <DefinedValues>
                                                      <DefinedValue>
                                                        <ColumnReference Column="Expr1033" />
                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODUNIT].[CNAME]">
                                                          <Identifier>
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                                          </Identifier>
                                                        </ScalarOperator>
                                                      </DefinedValue>
                                                    </DefinedValues>
                                                    <RelOp AvgRowSize="408" EstimateCPU="0.000700292" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.28498" LogicalOp="Left Outer Join" NodeId="56" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.179353">
                                                      <OutputList>
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                        <ColumnReference Column="Expr1023" />
                                                        <ColumnReference Column="Expr1028" />
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                                        <ColumnReference Column="Expr1044" />
                                                        <ColumnReference Column="Expr1046" />
                                                        <ColumnReference Column="Expr1052" />
                                                        <ColumnReference Column="Expr1053" />
                                                        <ColumnReference Column="Expr1054" />
                                                        <ColumnReference Column="Expr1055" />
                                                      </OutputList>
                                                      <NestedLoops Optimized="false">
                                                        <Predicate>
                                                          <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODUNIT].[CODE]=[PILOT2004].[dbo].[STKPRHS2].[UNIT]">
                                                            <Compare CompareOp="EQ">
                                                              <ScalarOperator>
                                                                <Identifier>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CODE" />
                                                                </Identifier>
                                                              </ScalarOperator>
                                                              <ScalarOperator>
                                                                <Identifier>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="UNIT" />
                                                                </Identifier>
                                                              </ScalarOperator>
                                                            </Compare>
                                                          </ScalarOperator>
                                                        </Predicate>
                                                        <RelOp AvgRowSize="405" EstimateCPU="3.28498E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.28498" LogicalOp="Compute Scalar" NodeId="57" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.17001">
                                                          <OutputList>
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="UNIT" />
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                            <ColumnReference Column="Expr1023" />
                                                            <ColumnReference Column="Expr1028" />
                                                            <ColumnReference Column="Expr1044" />
                                                            <ColumnReference Column="Expr1046" />
                                                            <ColumnReference Column="Expr1052" />
                                                            <ColumnReference Column="Expr1053" />
                                                            <ColumnReference Column="Expr1054" />
                                                            <ColumnReference Column="Expr1055" />
                                                          </OutputList>
                                                          <ComputeScalar>
                                                            <DefinedValues>
                                                              <DefinedValue>
                                                                <ColumnReference Column="Expr1028" />
                                                                <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODQTY].[CQTY]">
                                                                  <Identifier>
                                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="CQTY" />
                                                                  </Identifier>
                                                                </ScalarOperator>
                                                              </DefinedValue>
                                                            </DefinedValues>
                                                            <RelOp AvgRowSize="405" EstimateCPU="1.37312E-05" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.28498" LogicalOp="Left Outer Join" NodeId="58" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.170008">
                                                              <OutputList>
                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="UNIT" />
                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                                <ColumnReference Column="Expr1023" />
                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="CQTY" />
                                                                <ColumnReference Column="Expr1044" />
                                                                <ColumnReference Column="Expr1046" />
                                                                <ColumnReference Column="Expr1052" />
                                                                <ColumnReference Column="Expr1053" />
                                                                <ColumnReference Column="Expr1054" />
                                                                <ColumnReference Column="Expr1055" />
                                                              </OutputList>
                                                              <NestedLoops Optimized="false">
                                                                <OuterReferences>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="GWN" />
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                </OuterReferences>
                                                                <RelOp AvgRowSize="405" EstimateCPU="3.28498E-07" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.28498" LogicalOp="Compute Scalar" NodeId="59" Parallel="false" PhysicalOp="Compute Scalar" EstimatedTotalSubtreeCost="0.0659117">
                                                                  <OutputList>
                                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="GWN" />
                                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="UNIT" />
                                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                                    <ColumnReference Column="Expr1023" />
                                                                    <ColumnReference Column="Expr1044" />
                                                                    <ColumnReference Column="Expr1046" />
                                                                    <ColumnReference Column="Expr1052" />
                                                                    <ColumnReference Column="Expr1053" />
                                                                    <ColumnReference Column="Expr1054" />
                                                                    <ColumnReference Column="Expr1055" />
                                                                  </OutputList>
                                                                  <ComputeScalar>
                                                                    <DefinedValues>
                                                                      <DefinedValue>
                                                                        <ColumnReference Column="Expr1023" />
                                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[ORDERI]">
                                                                          <Identifier>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                                          </Identifier>
                                                                        </ScalarOperator>
                                                                      </DefinedValue>
                                                                      <DefinedValue>
                                                                        <ColumnReference Column="Expr1044" />
                                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[OUQTY]">
                                                                          <Identifier>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OUQTY" />
                                                                          </Identifier>
                                                                        </ScalarOperator>
                                                                      </DefinedValue>
                                                                      <DefinedValue>
                                                                        <ColumnReference Column="Expr1046" />
                                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[PROD]">
                                                                          <Identifier>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                          </Identifier>
                                                                        </ScalarOperator>
                                                                      </DefinedValue>
                                                                      <DefinedValue>
                                                                        <ColumnReference Column="Expr1052" />
                                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[SPEC]">
                                                                          <Identifier>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="SPEC" />
                                                                          </Identifier>
                                                                        </ScalarOperator>
                                                                      </DefinedValue>
                                                                      <DefinedValue>
                                                                        <ColumnReference Column="Expr1053" />
                                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[QTY]">
                                                                          <Identifier>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="QTY" />
                                                                          </Identifier>
                                                                        </ScalarOperator>
                                                                      </DefinedValue>
                                                                      <DefinedValue>
                                                                        <ColumnReference Column="Expr1054" />
                                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[VPNO]">
                                                                          <Identifier>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="VPNO" />
                                                                          </Identifier>
                                                                        </ScalarOperator>
                                                                      </DefinedValue>
                                                                      <DefinedValue>
                                                                        <ColumnReference Column="Expr1055" />
                                                                        <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[REMARK]">
                                                                          <Identifier>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="REMARK" />
                                                                          </Identifier>
                                                                        </ScalarOperator>
                                                                      </DefinedValue>
                                                                    </DefinedValues>
                                                                    <RelOp AvgRowSize="373" EstimateCPU="1.37312E-05" EstimateIO="0" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.28498" LogicalOp="Inner Join" NodeId="60" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.06591">
                                                                      <OutputList>
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="GWN" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="SPEC" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="QTY" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="UNIT" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="REMARK" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="VPNO" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OUQTY" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                                      </OutputList>
                                                                      <NestedLoops Optimized="false">
                                                                        <OuterReferences>
                                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ERI" />
                                                                        </OuterReferences>
                                                                        <RelOp AvgRowSize="27" EstimateCPU="0.000160613" EstimateIO="0.003125" EstimateRebinds="4.14795" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="3.28498" LogicalOp="Index Seek" NodeId="61" Parallel="false" PhysicalOp="Index Seek" EstimatedTotalSubtreeCost="0.0136538" TableCardinality="26270">
                                                                          <OutputList>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ERI" />
                                                                          </OutputList>
                                                                          <IndexScan Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                                                            <DefinedValues>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ERI" />
                                                                              </DefinedValue>
                                                                            </DefinedValues>
                                                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Index="[STKPRHS2_0]" IndexKind="NonClustered" />
                                                                            <SeekPredicates>
                                                                              <SeekPredicateNew>
                                                                                <SeekKeys>
                                                                                  <Prefix ScanType="EQ">
                                                                                    <RangeColumns>
                                                                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="MASTERI" />
                                                                                    </RangeColumns>
                                                                                    <RangeExpressions>
                                                                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS1].[ERI]">
                                                                                        <Identifier>
                                                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS1]" Column="ERI" />
                                                                                        </Identifier>
                                                                                      </ScalarOperator>
                                                                                    </RangeExpressions>
                                                                                  </Prefix>
                                                                                </SeekKeys>
                                                                              </SeekPredicateNew>
                                                                            </SeekPredicates>
                                                                          </IndexScan>
                                                                        </RelOp>
                                                                        <RelOp AvgRowSize="479" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="15.9109" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Clustered Index Seek" NodeId="63" Parallel="false" PhysicalOp="Clustered Index Seek" EstimatedTotalSubtreeCost="0.0521855" TableCardinality="26270">
                                                                          <OutputList>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="GWN" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="SPEC" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="QTY" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="UNIT" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="REMARK" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="VPNO" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OUQTY" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                                          </OutputList>
                                                                          <IndexScan Lookup="true" Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                                                            <DefinedValues>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="GWN" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="SPEC" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="QTY" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="UNIT" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="REMARK" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="VPNO" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OUQTY" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="OTHERUNIT" />
                                                                              </DefinedValue>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                                              </DefinedValue>
                                                                            </DefinedValues>
                                                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Index="[STKPRHS2_PK]" TableReferenceId="-1" IndexKind="Clustered" />
                                                                            <SeekPredicates>
                                                                              <SeekPredicateNew>
                                                                                <SeekKeys>
                                                                                  <Prefix ScanType="EQ">
                                                                                    <RangeColumns>
                                                                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ERI" />
                                                                                    </RangeColumns>
                                                                                    <RangeExpressions>
                                                                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[ERI]">
                                                                                        <Identifier>
                                                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ERI" />
                                                                                        </Identifier>
                                                                                      </ScalarOperator>
                                                                                    </RangeExpressions>
                                                                                  </Prefix>
                                                                                </SeekKeys>
                                                                              </SeekPredicateNew>
                                                                            </SeekPredicates>
                                                                          </IndexScan>
                                                                        </RelOp>
                                                                      </NestedLoops>
                                                                    </RelOp>
                                                                  </ComputeScalar>
                                                                </RelOp>
                                                                <RelOp AvgRowSize="15" EstimateCPU="1E-07" EstimateIO="0" EstimateRebinds="15.9109" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Top" NodeId="100" Parallel="false" PhysicalOp="Top" EstimatedTotalSubtreeCost="0.104025">
                                                                  <OutputList>
                                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="CQTY" />
                                                                  </OutputList>
                                                                  <Top RowCount="false" IsPercent="false" WithTies="false">
                                                                    <TopExpression>
                                                                      <ScalarOperator ScalarString="(1)">
                                                                        <Const ConstValue="(1)" />
                                                                      </ScalarOperator>
                                                                    </TopExpression>
                                                                    <RelOp AvgRowSize="15" EstimateCPU="8.15333E-06" EstimateIO="0" EstimateRebinds="15.9109" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Inner Join" NodeId="101" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.104024">
                                                                      <OutputList>
                                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="CQTY" />
                                                                      </OutputList>
                                                                      <NestedLoops Optimized="false">
                                                                        <OuterReferences>
                                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="ERI" />
                                                                        </OuterReferences>
                                                                        <RelOp AvgRowSize="27" EstimateCPU="0.000159146" EstimateIO="0.003125" EstimateRebinds="15.9109" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Index Seek" NodeId="102" Parallel="false" PhysicalOp="Index Seek" EstimatedTotalSubtreeCost="0.0270381" TableCardinality="11716">
                                                                          <OutputList>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="ERI" />
                                                                          </OutputList>
                                                                          <IndexScan Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                                                            <DefinedValues>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="ERI" />
                                                                              </DefinedValue>
                                                                            </DefinedValues>
                                                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Index="[PRODQTY_0]" IndexKind="NonClustered" />
                                                                            <SeekPredicates>
                                                                              <SeekPredicateNew>
                                                                                <SeekKeys>
                                                                                  <Prefix ScanType="EQ">
                                                                                    <RangeColumns>
                                                                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="GWN" />
                                                                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="PROD" />
                                                                                    </RangeColumns>
                                                                                    <RangeExpressions>
                                                                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[GWN]">
                                                                                        <Identifier>
                                                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="GWN" />
                                                                                        </Identifier>
                                                                                      </ScalarOperator>
                                                                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[PROD]">
                                                                                        <Identifier>
                                                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                                                        </Identifier>
                                                                                      </ScalarOperator>
                                                                                    </RangeExpressions>
                                                                                  </Prefix>
                                                                                </SeekKeys>
                                                                              </SeekPredicateNew>
                                                                            </SeekPredicates>
                                                                          </IndexScan>
                                                                        </RelOp>
                                                                        <RelOp AvgRowSize="15" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="25" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Clustered Index Seek" NodeId="104" Parallel="false" PhysicalOp="Clustered Index Seek" EstimatedTotalSubtreeCost="0.0795001" TableCardinality="11716">
                                                                          <OutputList>
                                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="CQTY" />
                                                                          </OutputList>
                                                                          <IndexScan Lookup="true" Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                                                            <DefinedValues>
                                                                              <DefinedValue>
                                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="CQTY" />
                                                                              </DefinedValue>
                                                                            </DefinedValues>
                                                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Index="[PRODQTY_PK]" TableReferenceId="-1" IndexKind="Clustered" />
                                                                            <SeekPredicates>
                                                                              <SeekPredicateNew>
                                                                                <SeekKeys>
                                                                                  <Prefix ScanType="EQ">
                                                                                    <RangeColumns>
                                                                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="ERI" />
                                                                                    </RangeColumns>
                                                                                    <RangeExpressions>
                                                                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODQTY].[ERI]">
                                                                                        <Identifier>
                                                                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODQTY]" Column="ERI" />
                                                                                        </Identifier>
                                                                                      </ScalarOperator>
                                                                                    </RangeExpressions>
                                                                                  </Prefix>
                                                                                </SeekKeys>
                                                                              </SeekPredicateNew>
                                                                            </SeekPredicates>
                                                                          </IndexScan>
                                                                        </RelOp>
                                                                      </NestedLoops>
                                                                    </RelOp>
                                                                  </Top>
                                                                </RelOp>
                                                              </NestedLoops>
                                                            </RelOp>
                                                          </ComputeScalar>
                                                        </RelOp>
                                                        <RelOp AvgRowSize="20" EstimateCPU="0.0001346" EstimateIO="0.0032035" EstimateRebinds="0" EstimateRewinds="15.9109" EstimatedExecutionMode="Row" EstimateRows="51" LogicalOp="Clustered Index Scan" NodeId="112" Parallel="false" PhysicalOp="Clustered Index Scan" EstimatedTotalSubtreeCost="0.00547971" TableCardinality="51">
                                                          <OutputList>
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CODE" />
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                                          </OutputList>
                                                          <IndexScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                                            <DefinedValues>
                                                              <DefinedValue>
                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CODE" />
                                                              </DefinedValue>
                                                              <DefinedValue>
                                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                                              </DefinedValue>
                                                            </DefinedValues>
                                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Index="[PRODUNIT_PK]" TableReferenceId="1" IndexKind="Clustered" />
                                                          </IndexScan>
                                                        </RelOp>
                                                      </NestedLoops>
                                                    </RelOp>
                                                  </ComputeScalar>
                                                </RelOp>
                                                <RelOp AvgRowSize="21" EstimateCPU="1E-07" EstimateIO="0" EstimateRebinds="14.4568" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Top" NodeId="117" Parallel="false" PhysicalOp="Top" EstimatedTotalSubtreeCost="0.0455989">
                                                  <OutputList>
                                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDCORD2]" Column="CODE" />
                                                  </OutputList>
                                                  <Top RowCount="false" IsPercent="false" WithTies="false">
                                                    <TopExpression>
                                                      <ScalarOperator ScalarString="(1)">
                                                        <Const ConstValue="(1)" />
                                                      </ScalarOperator>
                                                    </TopExpression>
                                                    <RelOp AvgRowSize="21" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="14.4568" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Clustered Index Seek" NodeId="118" Parallel="false" PhysicalOp="Clustered Index Seek" EstimatedTotalSubtreeCost="0.0455973" TableCardinality="1817">
                                                      <OutputList>
                                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDCORD2]" Column="CODE" />
                                                      </OutputList>
                                                      <IndexScan Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                                        <DefinedValues>
                                                          <DefinedValue>
                                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDCORD2]" Column="CODE" />
                                                          </DefinedValue>
                                                        </DefinedValues>
                                                        <Object Database="[PILOT2004]" Schema="[dbo]" Table="[ORDCORD2]" Index="[ORDCORD2_PK]" IndexKind="Clustered" />
                                                        <SeekPredicates>
                                                          <SeekPredicateNew>
                                                            <SeekKeys>
                                                              <Prefix ScanType="EQ">
                                                                <RangeColumns>
                                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDCORD2]" Column="ERI" />
                                                                </RangeColumns>
                                                                <RangeExpressions>
                                                                  <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[ORDERI]">
                                                                    <Identifier>
                                                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="ORDERI" />
                                                                    </Identifier>
                                                                  </ScalarOperator>
                                                                </RangeExpressions>
                                                              </Prefix>
                                                            </SeekKeys>
                                                          </SeekPredicateNew>
                                                        </SeekPredicates>
                                                      </IndexScan>
                                                    </RelOp>
                                                  </Top>
                                                </RelOp>
                                              </NestedLoops>
                                            </RelOp>
                                          </ComputeScalar>
                                        </RelOp>
                                        <RelOp AvgRowSize="20" EstimateCPU="0.0001346" EstimateIO="0.0032035" EstimateRebinds="0" EstimateRewinds="14.4568" EstimatedExecutionMode="Row" EstimateRows="51" LogicalOp="Clustered Index Scan" NodeId="123" Parallel="false" PhysicalOp="Clustered Index Scan" EstimatedTotalSubtreeCost="0.00528399" TableCardinality="51">
                                          <OutputList>
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CODE" />
                                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                          </OutputList>
                                          <IndexScan Ordered="false" ForcedIndex="false" ForceScan="false" NoExpandHint="false">
                                            <DefinedValues>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CODE" />
                                              </DefinedValue>
                                              <DefinedValue>
                                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Column="CNAME" />
                                              </DefinedValue>
                                            </DefinedValues>
                                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUNIT]" Index="[PRODUNIT_PK]" TableReferenceId="2" IndexKind="Clustered" />
                                          </IndexScan>
                                        </RelOp>
                                      </NestedLoops>
                                    </RelOp>
                                  </ComputeScalar>
                                </RelOp>
                                <RelOp AvgRowSize="28" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="4.84744" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Index Seek" NodeId="128" Parallel="false" PhysicalOp="Index Seek" EstimatedTotalSubtreeCost="0.0153919" TableCardinality="8609">
                                  <OutputList>
                                    <ColumnReference Column="IsBaseRow1048" />
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="ERI" />
                                  </OutputList>
                                  <IndexScan Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                    <DefinedValues>
                                      <DefinedValue>
                                        <ColumnReference Column="IsBaseRow1048" />
                                      </DefinedValue>
                                      <DefinedValue>
                                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="ERI" />
                                      </DefinedValue>
                                    </DefinedValues>
                                    <Object Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Index="[PRODUCT_0]" IndexKind="NonClustered" />
                                    <SeekPredicates>
                                      <SeekPredicateNew>
                                        <SeekKeys>
                                          <Prefix ScanType="EQ">
                                            <RangeColumns>
                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="CODE" />
                                            </RangeColumns>
                                            <RangeExpressions>
                                              <ScalarOperator ScalarString="[PILOT2004].[dbo].[STKPRHS2].[PROD]">
                                                <Identifier>
                                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[STKPRHS2]" Column="PROD" />
                                                </Identifier>
                                              </ScalarOperator>
                                            </RangeExpressions>
                                          </Prefix>
                                        </SeekKeys>
                                      </SeekPredicateNew>
                                    </SeekPredicates>
                                  </IndexScan>
                                </RelOp>
                              </NestedLoops>
                            </RelOp>
                            <RelOp AvgRowSize="86" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="4.84744" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Clustered Index Seek" NodeId="130" Parallel="false" PhysicalOp="Clustered Index Seek" EstimatedTotalSubtreeCost="0.0160575" TableCardinality="8609">
                              <OutputList>
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="CNAME" />
                              </OutputList>
                              <IndexScan Lookup="true" Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                                <DefinedValues>
                                  <DefinedValue>
                                    <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="CNAME" />
                                  </DefinedValue>
                                </DefinedValues>
                                <Object Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Index="[PRODUCT_PK]" TableReferenceId="-1" IndexKind="Clustered" />
                                <SeekPredicates>
                                  <SeekPredicateNew>
                                    <SeekKeys>
                                      <Prefix ScanType="EQ">
                                        <RangeColumns>
                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="ERI" />
                                        </RangeColumns>
                                        <RangeExpressions>
                                          <ScalarOperator ScalarString="[PILOT2004].[dbo].[PRODUCT].[ERI]">
                                            <Identifier>
                                              <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[PRODUCT]" Column="ERI" />
                                            </Identifier>
                                          </ScalarOperator>
                                        </RangeExpressions>
                                      </Prefix>
                                    </SeekKeys>
                                  </SeekPredicateNew>
                                </SeekPredicates>
                              </IndexScan>
                            </RelOp>
                          </NestedLoops>
                        </RelOp>
                      </ComputeScalar>
                    </RelOp>
                  </NestedLoops>
                </RelOp>
                <RelOp AvgRowSize="9" EstimateCPU="7.95355E-05" EstimateIO="0" EstimateRebinds="4.84744" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Inner Join" NodeId="137" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.416292">
                  <OutputList />
                  <NestedLoops Optimized="false">
                    <OuterReferences>
                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="MASTERI" />
                    </OuterReferences>
                    <RelOp AvgRowSize="27" EstimateCPU="7.95355E-05" EstimateIO="0" EstimateRebinds="4.84744" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="19.0276" LogicalOp="Inner Join" NodeId="138" Parallel="false" PhysicalOp="Nested Loops" EstimatedTotalSubtreeCost="0.370112">
                      <OutputList>
                        <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="MASTERI" />
                      </OutputList>
                      <NestedLoops Optimized="false">
                        <OuterReferences>
                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="ERI" />
                        </OuterReferences>
                        <RelOp AvgRowSize="27" EstimateCPU="0.00017793" EstimateIO="0.003125" EstimateRebinds="4.84744" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="19.0276" LogicalOp="Index Seek" NodeId="139" Parallel="false" PhysicalOp="Index Seek" EstimatedTotalSubtreeCost="0.0160909" TableCardinality="64732">
                          <OutputList>
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="ERI" />
                          </OutputList>
                          <IndexScan Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                            <DefinedValues>
                              <DefinedValue>
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="ERI" />
                              </DefinedValue>
                            </DefinedValues>
                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Index="[ORDBOM2_6]" IndexKind="NonClustered" />
                            <SeekPredicates>
                              <SeekPredicateNew>
                                <SeekKeys>
                                  <Prefix ScanType="EQ">
                                    <RangeColumns>
                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="SUBPROD" />
                                    </RangeColumns>
                                    <RangeExpressions>
                                      <ScalarOperator ScalarString="[Expr1046]">
                                        <Identifier>
                                          <ColumnReference Column="Expr1046" />
                                        </Identifier>
                                      </ScalarOperator>
                                    </RangeExpressions>
                                  </Prefix>
                                </SeekKeys>
                              </SeekPredicateNew>
                            </SeekPredicates>
                          </IndexScan>
                        </RelOp>
                        <RelOp AvgRowSize="19" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="110.263" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Clustered Index Seek" NodeId="141" Parallel="false" PhysicalOp="Clustered Index Seek" EstimatedTotalSubtreeCost="0.353556" TableCardinality="64732">
                          <OutputList>
                            <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="MASTERI" />
                          </OutputList>
                          <IndexScan Lookup="true" Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                            <DefinedValues>
                              <DefinedValue>
                                <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="MASTERI" />
                              </DefinedValue>
                            </DefinedValues>
                            <Object Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Index="[ORDBOM2_PK]" TableReferenceId="-1" IndexKind="Clustered" />
                            <SeekPredicates>
                              <SeekPredicateNew>
                                <SeekKeys>
                                  <Prefix ScanType="EQ">
                                    <RangeColumns>
                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="ERI" />
                                    </RangeColumns>
                                    <RangeExpressions>
                                      <ScalarOperator ScalarString="[PILOT2004].[dbo].[ORDBOM2].[ERI]">
                                        <Identifier>
                                          <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="ERI" />
                                        </Identifier>
                                      </ScalarOperator>
                                    </RangeExpressions>
                                  </Prefix>
                                </SeekKeys>
                              </SeekPredicateNew>
                            </SeekPredicates>
                          </IndexScan>
                        </RelOp>
                      </NestedLoops>
                    </RelOp>
                    <RelOp AvgRowSize="9" EstimateCPU="0.0001581" EstimateIO="0.003125" EstimateRebinds="110.263" EstimateRewinds="0" EstimatedExecutionMode="Row" EstimateRows="1" LogicalOp="Index Seek" NodeId="145" Parallel="false" PhysicalOp="Index Seek" EstimatedTotalSubtreeCost="0.0457156" TableCardinality="1554">
                      <OutputList />
                      <IndexScan Ordered="true" ScanDirection="FORWARD" ForcedIndex="false" ForceSeek="false" ForceScan="false" NoExpandHint="false" Storage="RowStore">
                        <DefinedValues />
                        <Object Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM1]" Index="[ORDBOM1_2]" IndexKind="NonClustered" />
                        <SeekPredicates>
                          <SeekPredicateNew>
                            <SeekKeys>
                              <Prefix ScanType="EQ">
                                <RangeColumns>
                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM1]" Column="SRCERI" />
                                  <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM1]" Column="ERI" />
                                </RangeColumns>
                                <RangeExpressions>
                                  <ScalarOperator ScalarString="[Expr1023]">
                                    <Identifier>
                                      <ColumnReference Column="Expr1023" />
                                    </Identifier>
                                  </ScalarOperator>
                                  <ScalarOperator ScalarString="[PILOT2004].[dbo].[ORDBOM2].[MASTERI]">
                                    <Identifier>
                                      <ColumnReference Database="[PILOT2004]" Schema="[dbo]" Table="[ORDBOM2]" Column="MASTERI" />
                                    </Identifier>
                                  </ScalarOperator>
                                </RangeExpressions>
                              </Prefix>
                            </SeekKeys>
                          </SeekPredicateNew>
                        </SeekPredicates>
                      </IndexScan>
                    </RelOp>
                  </NestedLoops>
                </RelOp>
              </NestedLoops>
            </RelOp>
          </QueryPlan>
        </StmtSimple>
      </Statements>
    </Batch>
  </BatchSequence>
</ShowPlanXML>