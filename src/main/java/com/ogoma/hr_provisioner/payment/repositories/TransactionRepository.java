package com.ogoma.hr_provisioner.payment.repositories;

import com.ogoma.hr_provisioner.base.BaseRepository;
import com.ogoma.hr_provisioner.payment.entities.TransactionEntity;

public interface TransactionRepository extends BaseRepository<TransactionEntity> {

    public TransactionEntity findByMerchantRequestIDAndCheckoutRequestIDAndArchive(String merchantRequestId,String checkoutRequestId,boolean archive);
}
