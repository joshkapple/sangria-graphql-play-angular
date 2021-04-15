import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlaygroundWrapperComponent } from './playground-wrapper.component';

describe('PlaygroundWrapperComponent', () => {
  let component: PlaygroundWrapperComponent;
  let fixture: ComponentFixture<PlaygroundWrapperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlaygroundWrapperComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlaygroundWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
