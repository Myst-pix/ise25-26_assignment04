package de.uhd.campuscoffee.domain.model;

public class PointOfSale {
    // ...existing code...

    private Long id;
    private String name;
    private String street;
    private String city;
    private Double latitude;
    private Double longitude;

    // ...existing code...

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    // ...existing code...
}

