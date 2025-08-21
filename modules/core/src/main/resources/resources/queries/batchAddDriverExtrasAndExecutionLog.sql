declare @BOOKING_ID    numeric(19)
declare @EXTRA_CODE    varchar(100)
declare @CREATED_BY    numeric(19,0)
declare @AMOUNT        float
declare @HOST          varchar(100)
declare @SI_CODE       varchar(100)
declare @SI_TYPE_ID    numeric(19,0)

set @BOOKING_ID = :bookingId
set @EXTRA_CODE = :extraCode
set @CREATED_BY = :createdBy
set @AMOUNT = :amount
set @HOST = :host
set @SI_CODE = :siCode

set @SI_TYPE_ID = (
    select ID
    from SL_SPECIAL_INSTRUCTION_TYPES
    where CODE = @SI_CODE and IS_DELETED = 0
    )

delete from SL_JOB_EXTRAS
where JOB_ID = @BOOKING_ID
  and CODE = @EXTRA_CODE
  and IS_DELETED = 0

delete from SL_JOB_SPECIAL_INSTRUCTIONS
where JOB_ID = @BOOKING_ID
  and TYPE_ID = @SI_TYPE_ID
  and IS_DELETED = 0


if (@AMOUNT is not null)
    begin
        insert into SL_JOB_EXTRAS (UUID,
                                   IS_DELETED,
                                   CREATE_TS,
                                   CREATED_BY,
                                   VERSION,
                                   AMOUNT,
                                   DESCRIPTION,
                                   CREATION_DATE,
                                   JOB_ID,
                                   EXTRA_TYPE,
                                   CODE,
                                   SOURCE)
        select newid(1),
               0,
               getdate(),
               @CREATED_BY,
               0,
               @AMOUNT,
               'Driver Visibility auto boost pay',
               getdate(),
               job.ID,
               1,
               @EXTRA_CODE,
               30
        from SL_JOBS job
                 left join SL_JOB_EXTRAS extra on job.ID = extra.JOB_ID and
                                                  extra.CODE = @EXTRA_CODE and
                                                  extra.IS_DELETED = 0
        where job.ID = @BOOKING_ID
          and extra.ID is null
    end

insert into SL_JOB_SPECIAL_INSTRUCTIONS (UUID,
                                         IS_DELETED,
                                         CREATE_TS,
                                         CREATED_BY,
                                         VERSION,
                                         DESCRIPTION,
                                         JOB_ID,
                                         TYPE_ID)
select newid(1),
       0,
       getdate(),
       @CREATED_BY,
       0,
       CONVERT(varchar(100), @AMOUNT),
       job.ID,
       @SI_TYPE_ID
from SL_JOBS job
         left join SL_JOB_SPECIAL_INSTRUCTIONS si on si.JOB_ID = job.ID
    and si.TYPE_ID = @SI_TYPE_ID
    and si.IS_DELETED = 0
where job.ID = @BOOKING_ID
  and si.ID is null

update SL_JOBS
set UPDATED_BY = @CREATED_BY,
    UPDATE_TS = getdate(),
    VERSION = VERSION + 1
where ID = @BOOKING_ID

-- todo for future move this to rabbitmq event
-- todo add value to exec history
insert into SL_JOB_EVENT_LOG (UUID,
                              VERSION,
                              IS_DELETED,
                              CREATE_TS,
                              CREATED_BY,
                              JOB_ID,
                              CLASS_NAME,
                              DESCRIPTION,
                              DATE,
                              CREATED_ON)
select newID(1),
       0,
       0,
       getdate(),
       @CREATED_BY,
       ID,
       'AutoBoostVisibility',
       'Driver Visibility was automatically boosted to: ' + CONVERT(varchar(100), @AMOUNT),
       getdate(),
       @HOST
from SL_JOBS job
where ID = @BOOKING_ID
