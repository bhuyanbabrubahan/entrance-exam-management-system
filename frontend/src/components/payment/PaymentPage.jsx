import React, { useEffect, useState } from "react";

import Header from "../../components/layout/Header";
import UserInfoLogoutBar from "../../components/userdashboard/UserInfoLogoutBar";
import "./PaymentPage.css";

const PaymentPage = () => {

  const [paymentData, setPaymentData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {

    const token = localStorage.getItem("jwtToken");

    fetch("http://localhost:8080/api/payment/details", {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => res.json())
      .then(data => {
        setPaymentData(data);
        setLoading(false);
      });

  }, []);

  const handleMockPayment = (status) => {

    fetch("http://localhost:8080/api/payment/mock", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify({
        result: status
      })
    })
      .then(res => res.json())
      .then(data => {
        alert("Payment " + data.status);
        window.location.href = "/dashboard";
      });

  };

  /* ===== CREATE USER OBJECT FROM PAYMENT DATA ===== */

  const user = paymentData
    ? {
        firstName: paymentData.firstName,
        middleName: paymentData.middleName,
        lastName: paymentData.lastName,
        applicationNumber: paymentData.applicationNumber
      }
    : null;

  const loginInfo = paymentData
    ? {
        ipAddress: paymentData.ipAddress,
        loginTime: paymentData.loginTime
      }
    : null;

  return (

    <div className="payment-page">

      {/* HEADER */}
      <Header />

      {/* USER BAR */}
      {user && <UserInfoLogoutBar user={user} loginInfo={loginInfo} />}

      <div className="payment-container">

        <h2 className="payment-title">
          Examination Fee Payment
        </h2>

        {loading ? (

          <p className="loading-text">Loading payment details...</p>

        ) : (

          <div className="payment-card">

            {/* ===== CANDIDATE INFO ===== */}
            <div className="section-title">
              Candidate Information
            </div>

            <div className="payment-row">
              <span>Full Name</span>
              <span>
                {[paymentData.firstName, paymentData.middleName, paymentData.lastName]
                  .filter(Boolean)
                  .join(" ")}
              </span>
            </div>

            <div className="payment-row">
              <span>Application Number</span>
              <span>{paymentData.applicationNumber}</span>
            </div>

            <div className="payment-row">
              <span>Category</span>
              <span>{paymentData.category}</span>
            </div>

            <div className="payment-row">
              <span>Disability</span>
              <span>{paymentData.disability}</span>
            </div>

            {/* ===== PAYMENT INFO ===== */}
            <div className="section-title payment-section">
              Payment Details
            </div>

            <div className="payment-row fee">
              <span>Application Fee</span>
              <span>₹ {paymentData.feeAmount}</span>
            </div>

            {/* ===== BUTTONS ===== */}
            <div className="payment-buttons">

              <button
                className="pay-btn"
                onClick={() => handleMockPayment("SUCCESS")}
              >
                Pay Now
              </button>

              <button
                className="fail-btn"
                onClick={() => handleMockPayment("FAILED")}
              >
                Simulate Failure
              </button>

            </div>

          </div>

        )}

      </div>

    </div>

  );
};

export default PaymentPage;