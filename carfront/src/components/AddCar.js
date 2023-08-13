import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from '@mui/material';
import React, { useState } from 'react';

const AddCar = (props) => {
  const [open, setOpen] = useState(false);
  const [car, setCar] = useState({
    brand: '',
    model: '',
    color: '',
    year: '',
    fuel: '',
    price: '',
  });

  const handleClickOpen = () => {
    // 모달 폼 열기
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleChange = (event) => {
    // props로 넘어온 키 값에 대해 car State에 변경된 값 적용
    setCar({ ...car, [event.target.name]: event.target.value });
  };

  const handleSave = () => {
    props.addCar(car);
    handleClose();
  };
  // 모달 창 여는 버튼은 Dialog 컴포넌트 밖에 있어야 함
  return (
    <div>
      <button onClick={handleClickOpen}>New Car</button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>New car</DialogTitle>
        <DialogContent>
          <input
            placeholder="Brand"
            name="brand"
            value={car.brand}
            onChange={handleChange}
          />
          <br />
          <input
            placeholder="Model"
            name="model"
            value={car.model}
            onChange={handleChange}
          />
          <br />
          <input
            placeholder="Color"
            name="color"
            value={car.color}
            onChange={handleChange}
          />
          <br />
          <input
            placeholder="Year"
            name="year"
            value={car.year}
            onChange={handleChange}
          />
          <br />
          <input
            placeholder="Price"
            name="price"
            value={car.price}
            onChange={handleChange}
          />
          <br />
        </DialogContent>
        <DialogActions>
          <button onClick={handleClose}>Cancel</button>
          <button onClick={handleSave}>Save</button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default AddCar;
