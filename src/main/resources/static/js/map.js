// Populate provinces on modal open
function openAddressModal() {
    var addressModal = new bootstrap.Modal(document.getElementById('addressModal'));
    addressModal.show();

    // Fetch provinces when the modal is opened
    fetch('https://provinces.open-api.vn/api/p/')
      .then(response => response.json())
      .then(data => {
        const provinceSelect = document.getElementById('modalProvince');
        provinceSelect.innerHTML = '<option value="">Chọn Tỉnh/Thành phố</option>'; // Reset options
        data.forEach(province => {
          let option = document.createElement('option');
          option.value = province.code; // Store the province code
          option.text = province.name;
          provinceSelect.appendChild(option);
        });
      })
      .catch(error => console.error('Error fetching provinces:', error));
  }

  // Update districts based on selected province in modal
  function updateModalDistricts() {
    const provinceSelect = document.getElementById('modalProvince');
    const districtSelect = document.getElementById('modalDistrict');
    const wardSelect = document.getElementById('modalWard');
    const selectedProvinceCode = provinceSelect.value;

    // Clear previous options
    districtSelect.innerHTML = '<option value="">Chọn Quận/Huyện</option>';
    wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>';
    districtSelect.disabled = true;
    wardSelect.disabled = true;

    if (selectedProvinceCode) {
      fetch(`https://provinces.open-api.vn/api/p/${selectedProvinceCode}?depth=2`)
        .then(response => response.json())
        .then(data => {
          districtSelect.disabled = false;
          data.districts.forEach(district => {
            let option = document.createElement('option');
            option.value = district.code; // Store the district code
            option.text = district.name;
            districtSelect.appendChild(option);
          });
        })
        .catch(error => console.error('Error fetching districts:', error));
    }
  }

  // Update wards based on selected district in modal
  function updateModalWards() {
    const districtSelect = document.getElementById('modalDistrict');
    const wardSelect = document.getElementById('modalWard');
    const selectedDistrictCode = districtSelect.value;

    // Clear previous options
    wardSelect.innerHTML = '<option value="">Chọn Phường/Xã</option>';
    wardSelect.disabled = true;

    if (selectedDistrictCode) {
      fetch(`https://provinces.open-api.vn/api/d/${selectedDistrictCode}?depth=2`)
        .then(response => response.json())
        .then(data => {
          wardSelect.disabled = false;
          data.wards.forEach(ward => {
            let option = document.createElement('option');
            option.value = ward.name; // Store the ward name
            option.text = ward.name;
            wardSelect.appendChild(option);
          });
        })
        .catch(error => console.error('Error fetching wards:', error));
    }
  }

  // Save the address from the modal to the main form
  function saveAddress() {
    const provinceSelect = document.getElementById('modalProvince');
    const districtSelect = document.getElementById('modalDistrict');
    const wardSelect = document.getElementById('modalWard');
    const province = provinceSelect.options[provinceSelect.selectedIndex]?.text;
    const district = districtSelect.options[districtSelect.selectedIndex]?.text;
    const ward = wardSelect.options[wardSelect.selectedIndex]?.text;
    const addressDetail = document.getElementById('modalAddressDetail').value;

    let fullAddress = [];
    if (addressDetail) fullAddress.push(addressDetail);
    if (ward && ward !== 'Chọn Phường/Xã') fullAddress.push(ward);
    if (district && district !== 'Chọn Quận/Huyện') fullAddress.push(district);
    if (province && province !== 'Chọn Tỉnh/Thành phố') fullAddress.push(province);

    document.getElementById('addressInput').value = fullAddress.join(', ');

    var addressModal = bootstrap.Modal.getInstance(document.getElementById('addressModal'));
    addressModal.hide();
  }

  function previewImage(event) {
    var reader = new FileReader();
    reader.onload = function() {
      var output = document.getElementById('previewImg');
      output.src = reader.result;
    };
    reader.readAsDataURL(event.target.files[0]);
  }