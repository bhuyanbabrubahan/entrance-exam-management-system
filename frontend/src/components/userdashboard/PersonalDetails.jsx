import React, { useEffect, useState, useMemo } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Header from "../../components/layout/Header";
import UserInfoLogoutBar from "../../components/userdashboard/UserInfoLogoutBar";
import "../../styles/userdashboard/PersonalDeatils.css";


  const PersonalDetails = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [user, setUser] = useState(null);
  const [basicInfo, setBasicInfo] = useState(null);
  const [loginInfo, setLoginInfo] = useState(null);
  
  
  const [form, setForm] = useState({
    fatherName: "",
    motherName: "",
    nationality: "",
    category: "",
    maritalStatus: "",
    disability: "",
    correspondenceAddress: "",
    country: "",
    state: "",
    district: "",
    city: "",
    pincode: "",
    sameAsCorrespondence: false,
    permanentAddress: "",
  });

  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [cities, setCities] = useState([]);
  const [pincodes, setPincodes] = useState([]);
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [initialized, setInitialized] = useState(false);
  const [reopenAllowed, setReopenAllowed] = useState(false);
  const [correctionStatus, setCorrectionStatus] = useState(null);

  const pageMode = location.state?.mode || "EDIT";

  // ===== LOCK LOGIC =====
  const isLocked = useMemo(() => {
    if (loading) return true;
    if (reopenAllowed) return false;
    if (status === "COMPLETED") {
      return !(correctionStatus?.correctionActive && correctionStatus?.personalEditable);
    }
    return false;
  }, [status, correctionStatus, reopenAllowed, loading]);

  const { isViewOnly, isEditable } = useMemo(() => ({
    isViewOnly: isLocked,
    isEditable: !isLocked,
  }), [isLocked]);

  // ================= FETCH INITIAL DATA =================
  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      navigate("/login", { replace: true });
      return;
    }

    // Fetch dashboard info
    fetch("http://localhost:8080/api/user/dashboard/current", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => res.ok ? res.json() : Promise.reject("Failed to fetch dashboard"))
      .then(data => {
        setUser(data.user || null);
        setLoginInfo(data.loginInfo || null);
      })
      .catch(console.error);

    // Fetch personal details
    fetch("http://localhost:8080/api/user/personal-details/current", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => res.ok ? res.json() : Promise.reject("Unauthorized"))
      .then(data => {
        setBasicInfo(data || {});
        setStatus(data.personalStatus || "NOT_STARTED");
        setReopenAllowed(data.reopenAllowed === true);

        if (!initialized) {
          setForm(prev => ({
              ...prev,

              fatherName: data.fatherName || "",
              motherName: data.motherName || "",
              nationality: data.nationality || "",
              category: data.category || "",
              maritalStatus: data.maritalStatus || "",
              disability: data.disability || "",
              correspondenceAddress: data.correspondenceAddress || "",

              // ✅ USE IDS
              country: data.countryId || "",
              state: data.stateId || "",
              district: data.districtId || "",
              city: data.cityId || "",
              pincode: data.pincodeId || "",

              sameAsCorrespondence: data.sameAsCorrespondence ?? false,
              permanentAddress: data.permanentAddress || ""
            }));
          setInitialized(true);
        }

        setLoading(false);
      })
      .catch(err => {
        console.error("Error fetching personal details:", err);
        setLoading(false);
        navigate("/dashboard");
      });
  }, [navigate]); // ✅ removed `initialized` from deps

  // ================= FETCH COUNTRIES =================
    useEffect(() => {

      const token = localStorage.getItem("jwtToken");

      console.log("🔵 Fetching Countries...");

      fetch("http://localhost:8080/api/location/countries", {
        headers: { Authorization: `Bearer ${token}` },
      })
        .then(res => {
          console.log("Countries Response Status:", res.status);

          if (!res.ok) {
            throw new Error("Country API Failed");
          }
          return res.json();
        })
        .then(data => {
          console.log("✅ Countries:", data);
          setCountries(Array.isArray(data) ? data : []);
        })
        .catch(err => {
          console.error("❌ Country API Error:", err);
          setCountries([]);
        });

    }, []);

        useEffect(() => {

          const token = localStorage.getItem("jwtToken");

          if (!form.country) {
            setStates([]);
            return;
          }

          fetch(`http://localhost:8080/api/location/states/${form.country}`, {
            headers: { Authorization: `Bearer ${token}` },
          })
            .then(res => res.json())
            .then(data => {
              console.log("✅ States:", data);
              setStates(Array.isArray(data) ? data : []);
            })
            .catch(() => setStates([]));

        }, [form.country]);

  // ================= FETCH DISTRICTS =================
      useEffect(() => {
  const token = localStorage.getItem("jwtToken");

  if (!form.state) {
    setDistricts([]);
    return;
  }

  fetch(`http://localhost:8080/api/location/districts/${form.state}`, {
    headers: { Authorization: `Bearer ${token}` },
  })
    .then(res => res.json())
    .then(data => {
      setDistricts(Array.isArray(data) ? data : []);
    })
    .catch(() => setDistricts([]));

}, [form.state]);

      // ================= FETCH CITIES =================
    useEffect(() => {
  const token = localStorage.getItem("jwtToken");

  if (!form.district) {
    setCities([]);
    return;
  }

  fetch(`http://localhost:8080/api/location/cities/${form.district}`, {
    headers: { Authorization: `Bearer ${token}` },
  })
    .then(res => res.json())
    .then(data => {
      setCities(Array.isArray(data) ? data : []);
    })
    .catch(() => setCities([]));

}, [form.district]);

  // ================= FETCH PINCODES =================
useEffect(() => {
  const token = localStorage.getItem("jwtToken");

  if (!form.city) {
    setPincodes([]); // Clear pincodes if no city selected
    return;
  }

  const fetchPincodes = async () => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/location/pincodes/${form.city}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) throw new Error("Failed to fetch pincodes");

      const data = await res.json();
      const list = Array.isArray(data) ? data : [];

      console.log("Fetched Pincode List:", list);
      console.log("City ID Sent To API:", form.city);
      console.log("Saved Pincode ID:", form.pincode);
      console.log("Pincode IDs Returned:", list.map(p => p.id));

      setPincodes(list);

      // 🔥 Check if saved pincode exists
      if (form.pincode) {
        const exists = list.find((p) => String(p.id) === String(form.pincode));
        if (exists) {
          console.log("✅ Matching pincode found:", exists);
        } else {
          console.log("❌ Pincode not found in list");
        }
      }

    } catch (error) {
      console.error("Error fetching pincodes:", error);
      setPincodes([]); // Reset pincodes if fetching fails
    }
  };

  fetchPincodes();

}, [form.city]);



          // ================= CORRECTION STATUS =================
          useEffect(() => {

            const token = localStorage.getItem("jwtToken");

            const fetchCorrectionStatus = () => {

              console.log("🔵 Checking Correction Window");

              fetch("http://localhost:8080/api/user/correction-status", {
                headers: { Authorization: `Bearer ${token}` },
              })
                .then(res => {
                  console.log("Correction Status:", res.status);

                  if (!res.ok) throw new Error("Unauthorized");

                  return res.json();
                })
                .then(data => {
                  console.log("✅ Correction Status:", data);
                  setCorrectionStatus(data);
                })
                .catch(err =>
                  console.error("❌ Correction Status Error:", err)
                );
            };

            fetchCorrectionStatus();

            const interval = setInterval(fetchCorrectionStatus, 15000);

            return () => clearInterval(interval);

          }, []);
  // ================= HANDLERS =================
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(prev => {
      let updated = { ...prev, [name]: type === "checkbox" ? checked : value };

      if (name === "country") updated.state = updated.district = updated.city = updated.pincode = "";
      if (name === "state") updated.district = updated.city = updated.pincode = "";
      if (name === "district") updated.city = updated.pincode = "";
      if (name === "city") updated.pincode = "";

      if (name === "sameAsCorrespondence") updated.permanentAddress = checked ? updated.correspondenceAddress : prev.permanentAddress;
      if (name === "correspondenceAddress" && updated.sameAsCorrespondence) updated.permanentAddress = value;

      return updated;
    });
  };

  const handleSave = async () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) return navigate("/login", { replace: true });

    try {
      const res = await fetch("http://localhost:8080/api/user/personal-details/save", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({
          ...form,

          countryId: form.country,
          stateId: form.state,
          districtId: form.district,
          cityId: form.city,
          pincodeId: form.pincode
        }),
      });
      if (res.status === 401) throw new Error("Unauthorized");
      const data = await res.json();
      if (data.personalStatus) setStatus(data.personalStatus);
      alert("Draft saved successfully!");
    } catch (err) {
      console.error(err);
      alert("Failed to save draft. Please try again.");
    }
  };

  const handleSubmit = async () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) return navigate("/login", { replace: true });

    try {
      const res = await fetch("http://localhost:8080/api/user/personal-details/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({
          ...form,

          countryId: form.country,
          stateId: form.state,
          districtId: form.district,
          cityId: form.city,
          pincodeId: form.pincode
        }),
      });
      if (res.status === 401) throw new Error("Unauthorized");
      if (!res.ok) throw new Error("SubmitFailed");
      const data = await res.json();
      setStatus(data.personalStatus || "COMPLETED");
      alert("Personal Details submitted successfully!");
      navigate("/dashboard");
    } catch (err) {
      console.error(err);
      alert("Submission failed. Please try again.");
    }
  };

// ===== SELECTED PINCODE SAFE MATCH =====
const selectedPincode = useMemo(() => {
  if (!form.pincode) return null;
  if (!pincodes.length) return null;

  return pincodes.find(
    (p) => String(p.id) === String(form.pincode)
  ) || null;

}, [pincodes, form.pincode]);



  if (loading) return <div className="dashboard-loading">Loading...</div>;
  if (!basicInfo) return <div>Loading personal basic info...</div>;
  if (!user) return <div>User not found</div>;

  return (
    <>
      <Header />
      <UserInfoLogoutBar user={user} loginInfo={loginInfo} />

      <div className="page-title">
        {correctionStatus?.correctionActive && correctionStatus?.personalEditable && (
          <div className="correction-banner">
            🟢 Correction Window Active — Personal section editable
          </div>
        )}
        <h2>🎓 PERSONAL DETAILS</h2>
      </div>

      {isViewOnly && (
        <div className="info-card">
          <div className="info-card-header">
            <h4>🔒 View Mode</h4>
            <p style={{ color: "red", fontSize: 12, textAlign: "center" }}>
            DEBUG → status: {status}, reopenAllowed: {String(reopenAllowed)}, isLocked: {String(isLocked)}
          </p>
            <span className={`status-pill ${status === "COMPLETED" ? "completed" : "not_completed"}`}>
               <span className="status-label">Status:</span> {status}
            </span>
          </div>

          <p>
            {reopenAllowed
              ? "This section has been reopened by the administrator. You may edit and resubmit."
              : "Personal details have been submitted and locked. Editing is disabled unless reopened by the administrator."}
          </p>
        </div>
      )}

      {/* ===== BASIC INFO ===== */}
      <div className="card read-only-card">
        <h4>🔒 BASIC INFORMATION</h4>

        <div className="card-row three-col">
          <div className="field-group">
            <label>First Name *</label>
            <input type="text" value={basicInfo.firstName || ""} readOnly />
          </div>
          <div className="field-group">
            <label>Middle Name</label>
            <input type="text" value={basicInfo.middleName || ""} readOnly />
          </div>
          <div className="field-group">
            <label>Last Name *</label>
            <input type="text" value={basicInfo.lastName || ""} readOnly />
          </div>
        </div>

        <div className="card-row three-col">
          <div className="field-group">
            <label>Gender *</label>
            <input type="text" value={basicInfo.gender || ""} readOnly />
          </div>
          <div className="field-group">
            <label>Date of Birth *</label>
            <input type="text" value={basicInfo.dob || ""} readOnly />
          </div>
          <div className="field-group">
            <label>Aadhaar Number *</label>
            <input type="text" value={basicInfo.aadharCard || ""} readOnly />
          </div>
        </div>

        <div className="card-row two-col">
          <div className="field-group">
            <label>Mobile Number *</label>
            <input type="text" value={basicInfo.mobileNumber || ""} readOnly />
          </div>
          <div className="field-group">
            <label>Email Address *</label>
            <input type="text" value={basicInfo.emailAddress || ""} readOnly />
          </div>
        </div>
      </div>

      {/* ===== PERSONAL INFO ===== */}
          <div className="card editable-card">
            <h4>🧾 PERSONAL INFORMATION</h4>

            <div className="card-row two-col">
              <div className="field-group">
                <label>Father’s Name *</label>
                <input
                  type="text"
                  name="fatherName"
                  value={form.fatherName}
                  onChange={handleChange}
                  disabled={!isEditable}
                  autoFocus={isEditable}
                />
              </div>
              <div className="field-group">
                <label>Mother’s Name *</label>
                <input
                  type="text"
                  name="motherName"
                  value={form.motherName}
                  onChange={handleChange}
                  disabled={!isEditable}
                />
              </div>
            </div>

            <div className="card-row two-col">
              <div className="field-group">
                <label>Nationality *</label>
                <select
                  name="nationality"
                  value={form.nationality}
                  onChange={handleChange}
                  disabled={!isEditable}
                  required
                >
                  <option value="" disabled>
                    Select Nationality
                  </option>
                  <option value="Indian">Indian</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              <div className="field-group">
                <label>Category *</label>
                <select
                  name="category"
                  value={form.category}
                  onChange={handleChange}
                  disabled={!isEditable}
                  required
                >
                  <option value="" disabled>
                    Select Category
                  </option>
                  <option value="GEN">GEN</option>
                  <option value="OBC">OBC</option>
                  <option value="SC">SC</option>
                  <option value="ST">ST</option>
                  <option value="EWS">EWS</option>
                </select>
              </div>
            </div>

            <div className="card-row two-col">
              <div className="field-group">
                <label>Marital Status *</label>
                <select
                  name="maritalStatus"
                  value={form.maritalStatus}
                  onChange={handleChange}
                  disabled={!isEditable}
                  required
                >
                  <option value="" disabled>
                    Select Status
                  </option>
                  <option value="Single">Single</option>
                  <option value="Married">Married</option>
                </select>
              </div>
              <div className="field-group">
                <label>Disability (PwD) *</label>
                <select
                  name="disability"
                  value={form.disability}
                  onChange={handleChange}
                  disabled={!isEditable}
                  required
                >
                  <option value="" disabled>
                    Select Option
                  </option>
                  <option value="No">No</option>
                  <option value="Yes">Yes</option>
                </select>
              </div>
            </div>
          </div>

          {/* ===== ADDRESS DETAILS ===== */}
<div className="card editable-card">
  <h4>🏠 ADDRESS DETAILS</h4>

  {/* ================= ADDRESS ================= */}
  <div className="card-row single">
    <div className="field-group">
      <label>Correspondence Address *</label>
      <input
        type="text"
        name="correspondenceAddress"
        value={form.correspondenceAddress || ""}
        onChange={handleChange}
        disabled={!isEditable}
      />
    </div>
  </div>

  {/* ================= LOCATION ================= */}
  <div className="card-row three-col">

    {/* ===== COUNTRY ===== */}
    <div className="field-group">
      <label>Country *</label>
      <select
        name="country"
        value={form.country || ""}
        onChange={handleChange}
        disabled={!isEditable}
        required
      >
        <option value="">Select Country</option>

        {Array.isArray(countries) &&
          countries.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
      </select>
    </div>

    {/* ===== STATE ===== */}
    <div className="field-group">
      <label>State / UT *</label>
      <select
        name="state"
        value={form.state || ""}
        onChange={handleChange}
        disabled={!isEditable}
      >
        <option value="">Select State</option>

        {Array.isArray(states) &&
          states.map((s) => (
            <option key={s.id} value={s.id}>
              {s.name}
            </option>
          ))}
      </select>
    </div>

    {/* ===== DISTRICT ===== */}
<div className="field-group">
  <label>District *</label>
  <select
    name="district"
    value={form.district || ""}
    onChange={handleChange}
    disabled={!isEditable}
  >
    <option value="">Select District</option>
    {districts.map((d) => (
      <option key={d.id} value={d.id}>
        {d.name}
      </option>
    ))}
  </select>
</div>

{/* ===== CITY ===== */}
<div className="field-group">
  <label>City *</label>
  <select
    name="city"
    value={form.city || ""}
    onChange={handleChange}
    disabled={!isEditable}
  >
    <option value="">Select City</option>
    {cities.map((c) => (
      <option key={c.id} value={c.id}>
        {c.name}
      </option>
    ))}
  </select>
</div>

{/* ===== PINCODE ===== */}
<div className="field-group">
  <label>Pincode *</label>
  <select
  name="pincode"
  value={form.pincode || ""}
  onChange={handleChange}
  disabled={!isEditable}
>
  <option value="">Select Pincode</option>
  {pincodes.map((p) => (
    <option key={p.id} value={p.id}>
      {p.name}
    </option>
  ))}
</select>
</div>
</div>

  {/* ================= SAME ADDRESS ================= */}
  <div className="card-row checkbox-row">
    <input
      type="checkbox"
      name="sameAsCorrespondence"
      checked={form.sameAsCorrespondence || false}
      onChange={handleChange}
      disabled={!isEditable}
    />
    <label>Same as Correspondence Address</label>
  </div>

  {/* ================= PERMANENT ADDRESS ================= */}
  <div className="card-row single">
    <div className="field-group">
      <label>Permanent Address</label>
      <input
        type="text"
        name="permanentAddress"
        value={
          form.sameAsCorrespondence
            ? form.correspondenceAddress || ""
            : form.permanentAddress || ""
        }
        onChange={handleChange}
        readOnly={form.sameAsCorrespondence || !isEditable}
        disabled={!isEditable}
      />
    </div>
  </div>

</div>

      {/* ===== IMPORTANT ===== */}
      <div className="info-card">
        <h4>⚠️ IMPORTANT</h4>
        <ul>
          <li>Please verify all details carefully</li>
          <li>Once submitted, editing will be locked</li>
          <li>Admin approval required for further changes</li>
        </ul>
      </div>

      {/* ===== ACTION BUTTONS ===== */}
      <div className="action-buttons">
     


        {/* 🔓 EDIT MODE */}
        {!isLocked && (
          <>
            <button
                  onClick={handleSave}
                  disabled={isLocked}
                >
                  SAVE DRAFT
            </button>

            <button
                onClick={handleSubmit}
                disabled={isLocked}
              >
                SUBMIT & LOCK
            </button>
          </>
        )}

        {/* 🔒 LOCKED MODE */}
        {isLocked && (
          <>
            <button disabled title="Editing allowed only if reopened by admin">
              🔒 Locked
            </button>

            <button
              className="btn outline back-dashboard-btn"
              onClick={() => navigate("/dashboard")}
            >
              ⬅ Back to Dashboard
            </button>
          </>
        )}


      </div>
    </>
  );
};

export default PersonalDetails;