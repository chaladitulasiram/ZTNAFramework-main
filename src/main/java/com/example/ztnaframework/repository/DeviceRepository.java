package com.example.ztnaframework.repository;


import com.example.ztnaframework.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<UserDevice, String> {
    // Find device by ID
}
