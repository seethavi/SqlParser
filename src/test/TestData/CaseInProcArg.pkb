CREATE OR REPLACE PACKAGE BODY DevGen
AS
   
   PROCEDURE PopulateTablesSub(
      pTableName                           VARCHAR2,
      pSubsetName                          VARCHAR2,
      pRowCount                            PLS_INTEGER,
      pDeep                                BOOLEAN,
      pDependeesOnly                       BOOLEAN,
      pDoingTableSubsets   IN OUT NOCOPY   lstTableSubset,
      pDoneTableSubsets    IN OUT NOCOPY   lstTableSubset
   )
   IS
 
   BEGIN
 


               DoInserts(
                  pTableName,
                  pRowCount,
                  pMaxErrors                   => pRowCount - 1,
                  pDeep                        => TRUE,
                  pOverrideGenValuesSpecs      => CASE
                     WHEN pSubsetName IS NULL
                        THEN NULL
                     ELSE GetTableSubsetDetails(pTableName, pSubsetName).OverrideGenValuesSpecs
                  END
               );
 
   
   END;

 
END;
/