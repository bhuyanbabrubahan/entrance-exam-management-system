const ConfirmModal = ({ actionType, onConfirm, onCancel }) => {
  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <h3>Confirm {actionType}</h3>
        <p>Are you sure you want to {actionType} this application?</p>

        <div className="modal-actions">
          <button onClick={onConfirm}>Yes</button>
          <button onClick={onCancel}>Cancel</button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;