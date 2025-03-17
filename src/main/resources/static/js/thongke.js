const categories = ['Điện thoại', 'Laptop', 'Tablet', 'Phụ kiện'];
const revenueData = [50000000, 80000000, 30000000, 20000000];
const quantityData = [150, 100, 80, 200];
const prices = {
            'Điện thoại': [500000, 20000000],
            'Laptop': [10000000, 50000000],
            'Tablet': [3000000, 15000000],
            'Phụ kiện': [50000, 1000000]
        };
    const revenueChart = new Chart(document.getElementById('revenueChart'), {
        type: 'bar',
            data: {
                labels: categories,
                datasets: [{
                    label: 'Doanh thu (VNĐ)',
                    data: revenueData,
                    backgroundColor: 'rgba(54, 162, 235, 0.6)'
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function (value) {
                                return value.toLocaleString() + ' VNĐ';
                            }
                        }
                    }
                }
            }
        });
    const revenuePie = new Chart(document.getElementById('revenueByCategory'), {
        type: 'pie',
            data: {
                labels: categories,
                datasets: [{
                    data: revenueData,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.6)',
                        'rgba(54, 162, 235, 0.6)',
                        'rgba(255, 206, 86, 0.6)',
                        'rgba(75, 192, 192, 0.6)'
                    ]
                }]
            }
    });
    const quantityChart = new Chart(document.getElementById('quantityByCategory'), {
        type: 'bar',
            data: {
                labels: categories,
                datasets: [{
                    label: 'Số lượng',
                    data: quantityData,
                    backgroundColor: 'rgba(75, 192, 192, 0.6)'
                }]
            },
            options: {
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    const allPrices = Object.values(prices).flat();
    const maxPrice = Math.max(...allPrices);
    const minPrice = Math.min(...allPrices);
    const avgPrice = allPrices.reduce((a, b) => a + b) / allPrices.length;
    document.getElementById('maxPrice').textContent = maxPrice.toLocaleString() + ' VNĐ';
    document.getElementById('minPrice').textContent = minPrice.toLocaleString() + ' VNĐ';
    document.getElementById('avgPrice').textContent = Math.round(avgPrice).toLocaleString() + ' VNĐ';